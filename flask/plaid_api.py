import requests
import json
import datetime

import settings

# Start date: 2014-03-27
# End date: 2014-07-21

class Plaid():
    def __init__(self, client_id=None, secret=None, username=None,
                 password=None,
                 bank_type=None):
        self.client_id = client_id or settings.CLIENT_ID
        self.secret = secret or settings.SECRET
        self.username = username or settings.USERNAME
        self.password = password or settings.PASSWORD
        self.bank_type = bank_type or settings.BANK_TYPE
        self.access_token = None
        self.from_date = datetime.datetime(year=2014, month=7, day=24)
        self.to_date = datetime.datetime.now()
        self.connect()

    def connect(self):
        data = {
            'client_id': self.client_id,
            'secret': self.secret,
            'username': self.username,
            'password': self.password,
            'type': self.bank_type,
            'options': {
                'login_only':True,
            },
        }
        r = requests.post('https://tartan.plaid.com/connect', json=data)
        if r.status_code == 200:
            json_data = json.loads(r.text)
            self.access_token = json_data['access_token']

    def get_transaction_data(self, from_date=None, to_date=None):
        if self.access_token is None:
            return None
        from_date = from_date or self.from_date
        to_date = to_date or self.to_date
        from_date_text = from_date.strftime('%Y-%m-%d')
        to_date_text = to_date.strftime('%Y-%m-%d')

        data = {
            'client_id': self.client_id,
            'secret': self.secret,
            'access_token': self.access_token,
            'options': {
                'gte': from_date_text,
                'lte': to_date_text,
            },
        }
        r = requests.post('https://tartan.plaid.com/connect/get', json=data)
        if r.status_code == 200:
            data = json.loads(r.text)
            return data
        print r.text
        return None
