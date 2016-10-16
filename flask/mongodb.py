from pymongo import MongoClient

MONGODB_HOST = 'localhost'
MONGODB_PORT = 27017

def connect_to_mongo(host=None, port=None):
    host = host or MONGODB_HOST
    port = port or MONGODB_PORT
    client = MongoClient()
    return client

def post_user(data):
    client = connect_to_mongo()
    db = client.db
    users = db.users
    result = users.insert_one(data)
    client.close()
    return result

def get_user(user):
    # client =  connect_to_mongo()
    # db = client.db
    # users = db.users
    pass

def post_transactions(data):
    client = connect_to_mongo()
    db = client.db
    transactions = db.transactions
    result = transactions.insert_many(data)
    pass

def get_transactions():
    client = connect_to_mongo()
    pass

def post_notification(data):
    client = connect_to_mongo()
    db = client.db
    notifications = db.notifications
    result = notifications.insert_one(data)
    client.close()
    return result

def get_unread_notifications():
    client = connect_to_mongo()
    notifications = []
    db = client.db
    notifications = db.notifications.find({'unread': True})
    data = []
    for notification in notifications:
        notification['_id'] = str(notification['_id'])
        data.append(notification)
    db.notifications.update(
    {
        'unread': True,
    },
    {
        '$set': {
            'unread': False
        }
    },
    upsert=False,
    multi=True,)
    client.close()
    return data
