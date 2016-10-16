from MongoKit import Connection

MONGODB_HOST = 'localhost'
MONGODB_PORT = 27017

def connect_to_mongo(host=None, port=None):
    host = host or MONGODB_HOST
    port = port or MONGODB_PORT
    connection = Connection(app.config['host'], app.config['port'])
    return connection

def post_user(connection, data):
    pass

def get_user(connection, _user):
    pass

def post_transactions(connection, data):
    pass

def get_transactions(connection):
    pass

def get_unread_notifications(connection):
    notifications = {}
    # get all unread notifcations
    return notifications
