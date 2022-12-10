import flask
from flask_login import login_user
from openidconnect import login_manager

@login_manager.user_loader
def load_user(token):
    return User.get(token)


users = {}

def do_login_user(user_id):
    user = User.get(user_id)
    login_user(user)
    next = flask.request.args.get('next')
    return flask.redirect(next or flask.url_for('profile'))



class User:

    token: str
    is_authenticated: bool
    is_active: bool
    is_anonymous: bool

    def __init__(self, token: str):
        self.token = token
        self.is_authenticated = True
        self.is_active = True
        self.is_anonymous = False

    def get_id(self):
        return self.token

    @staticmethod
    def get(token: str):
        if str in users:
            return users[str]
        else:
            user = User(token)
            users[token] = user
            return user

