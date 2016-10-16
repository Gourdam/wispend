from flask import Flask, render_template
from flask_restful import Resource, Api
import time
import threading
import random
import json
import numpy as np

from plaid_api import Plaid
import mongodb

app = Flask(__name__)
api = Api(app)


class Format(Resource):
    def get(self, total_budget):
        budget = {
            'Food and Drink': {
                'percent': 0.15,
                'budget':total_budget*0.15,
            },
            'Healthcare': {
                'percent': 0.05,
                'budget': total_budget*0.05,
            },
            'Recreation': {
                'percent': 0.05,
                'budget': total_budget*0.05,
            },
            'Shops': {
                'percent': 0.10,
                'budget': total_budget*0.10,
            },
            'Travel': {
                'percent': 0.10,
                'budget': total_budget*0.10,
            },
            'Payment': {
                'percent': 0.40,
                'budget': total_budget*0.4,
            },
            'Savings':{
                'percent': 0.15,
                'budget': total_budget*0.15,
            },
        }
        categories = {
            'Food and Drink': {
                'date': [],
                'amount': [],
            },
            'Healthcare': {
                'date': [],
                'amount': [],
            },
            'Recreation': {
                'date': [],
                'amount': [],
            },
            'Shops': {
                'date': [],
                'amount': [],
            },
            'Travel': {
                'date': [],
                'amount': [],
            },
            'Payment': {
                'date': [],
                'amount': [],
            }
        }
        data = None
        messages = []
        with open('Sample-transactions.JSON') as data_file:
            data = json.load(data_file)
        for transaction in data['transactions']:
            categories[transaction['category'][0]]['date'].append(transaction['date'])
            categories[transaction['category'][0]]['amount'].append(transaction['amount'])
        for category, value in categories.iteritems():
            category_sum = sum(value['amount'])
            # over budget case
            if category_sum > budget[category]['budget']:
                message = {
                    'title': 'You have exceeded your monthly allocated budget for {0}.'.format(category),
                    'timestamp': str(int(round(time.time() * 1000))),
                    'image': 'test',
                    'unread': True,
                }
                mongodb.post_notification(message)
                message['_id'] = str(message['_id'])
                messages.append(message)
                continue
            # projection to over budget case
            x = np.arange(len(value['amount']))
            y = np.array(value['amount'])
            m, b = np.polyfit(x,y,1)
            future_value = m*5 + b
            future_budget = budget[category]['budget']/6
            if future_value > future_budget:
                message = {
                    'title': 'You are projected to exceed your monthly planned budget for {0}.'.format(category),
                    'timestamp': str(int(round(time.time() * 1000))),
                    'image': 'test',
                    'unread': True,
                }
                mongodb.post_notification(message)
                message['_id'] = str(message['_id'])
                messages.append(message)
        return {'messages': messages}


class Notifications(Resource):
    def get(self):
        notifications = mongodb.get_unread_notifications()
        return {'notifications': notifications}

@app.route('/')
def index():
    return render_template('index.html')

api.add_resource(Notifications, '/notifications')
api.add_resource(Format, '/format/<int:total_budget>')

if __name__ == '__main__':
    app.run(debug=True)
