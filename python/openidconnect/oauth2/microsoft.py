import json
import requests
from flask import request, redirect,  session, url_for
import jwt
from .verify import decode_jwt
from openidconnect.view import app, csrf


@app.route('/login/oauth2/code/microsoft/init')
def ms_oauth2():
    # client_id=85d72273-d5ad-4567-9a5b-dc15d67fb1ba
    # redirect_uri=http%3A%2F%2Flocalhost%3A8888%2Flogin%2Foauth2%2Fcode%2Fmicrosoft
    # scope=openid
    auth_uri = (
        'https://login.microsoftonline.com/common/oauth2/v2.0/authorize'
        '?response_type=id_token'
        '&response_mode=form_post'
        '&state={}'
        '&nonce=robo_nonce'
        '&client_id={}'
        '&redirect_uri={}'
        '&scope={}'
    ).format(
        request.args['csrf_token'],
        app.config["MICROSOFT_CLIENT_ID"],
        app.config["MICROSOFT_REDIRECT_URI"],
        app.config["MICROSOFT_SCOPE"]
    )
    return redirect(auth_uri)


@app.route('/login/oauth2/code/microsoft', methods=['POST'])
def ms_oauth2callback():
    id_token = request.form['id_token']
    state = request.form['state']

    decoded = decode_jwt(
        app.config["MICROSOFT_AUDIENCE"],
        app.config["MICROSOFT_ISSUER"],
        id_token
    )
    session['credentials'] = decoded
    session['userid'] = decoded['sub']
    return redirect(url_for('start_page'))
