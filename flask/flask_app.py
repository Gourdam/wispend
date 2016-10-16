from flask import Flask
from flask_restful import Resource, Api
import time
import calendar
import threading
import random

from plaid_api import Plaid
import mongodb

app = Flask(__name__)
api = Api(app)

# Title
# date
# image to use
test = [
        {
            "title": "Don't do drugs",
            "timestamp": "1476590786",
            "image": "that_one",
        },
        {
            "title": "Memes are dremes",
            "timestamp": "1476590792",
            "image": "this_one",
        }
]
class Notifications(Resource):
    def get(self):
        test = {
            'title': str(random.randint(1, 10)),
            'timestamp': str(calendar.timegm(time.gmtime())),
            'image': 'test',
        }
        if random.randint(1, 5) == 3:
            mongodb.post_notification(test)
        notifications = mongodb.get_unread_notifications()
        return {'notifications': test}

api.add_resource(Notifications, '/notifications')

if __name__ == '__main__':
    app.run(debug=True)
