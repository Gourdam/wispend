from flask import Flask

from plaid_api import Plaid

app = Flask(__name__)

if __name__ == '__main__':
    app.run(debug=True)
