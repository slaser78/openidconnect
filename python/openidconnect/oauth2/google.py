import json
import requests
from flask import request, redirect,  session, url_for

from .verify import decode_jwt
from openidconnect import app
from openidconnect.user import do_login_user


@app.route('/login/oauth2/code/google/init')
def google_oauth2():
    auth_uri = ('https://accounts.google.com/o/oauth2/v2/auth?response_type=code'
                '&client_id={}&redirect_uri={}&scope={}').format(
        app.config["GOOGLE_CLIENT_ID"],
        app.config["GOOGLE_REDIRECT_URL"],
        app.config["GOOGLE_SCOPE"]
    )
    return redirect(auth_uri)


@app.route('/login/oauth2/code/google')
def google_oauth2callback():
    auth_code = request.args.get('code')
    data = {'code': auth_code,
            'client_id': app.config["GOOGLE_CLIENT_ID"],
            'client_secret': app.config["GOOGLE_CLIENT_SECRET"],
            'redirect_uri': app.config["GOOGLE_REDIRECT_URL"],
            'grant_type': 'authorization_code'}
    r = requests.post('https://oauth2.googleapis.com/token', data=data)
    j = json.loads(r.text)
    id_token = j["id_token"]

    decoded = decode_jwt(
        app.config["GOOGLE_AUDIENCE"],
        app.config["GOOGLE_ISSUER"],
        id_token
    )

    user_id = decoded['sub']
    session['credentials'] = decoded
    session['userid'] = user_id
    return do_login_user(user_id)
