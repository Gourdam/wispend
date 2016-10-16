from flask import Flask
from flask_restful import Resource, Api
import time
import calendar

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
            "timestamp": "1476590786"
            "image": "that_one",
        },
        {
            "title": "Memes are dremes",
            "timestamp": "1476590786"
            "image": "this_one",
        }
]
class Notifications(Resource):
    def get(self):
        notifications = mongodb.get_unread_notifications()
        timestamp = str(calendar.timegm(time.gmtime()))
        for notification in notifications:
            notification['timestamp'] = timestamp

        return {'notifications': test}

api.add_resource(Notifications, '/notifications')

if __name__ == '__main__':
    app.run(debug=True)
