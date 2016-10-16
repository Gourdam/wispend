from MongoKit import Connection

MONGODB_HOST = 'localhost'
MONGODB_PORT = 27017

def connect_to_mongo(host=None, port=None):
    host = host or MONGODB_HOST
    port = port or MONGODB_PORT
    connection = Connection(app.config['host'], app.config['port'])
    return connection

def post_user(data):
    connection = connect_to_mongo()
    pass

def get_user(user):
    connection =  connect_to_mongo()
    pass

def post_transactions(data):
    connection = connect_to_mongo()
    pass

def get_transactions():
    connection = connect_to_mongo()
    pass

def get_unread_notifications():
    connection = connect_to_mongo()
    notifications = []
    # get all unread notifcations
    return notifications
