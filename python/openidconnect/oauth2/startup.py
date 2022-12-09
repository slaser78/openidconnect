import json
import requests
from openidconnect.view import app, jwks


@app.before_first_request
def startup():
    # collect the public keys for Google and Microsoft
    ms_uri = app.config["MICROSOFT_KEYS_URI"]
    keys_reply = requests.get(ms_uri)
    keys = json.loads(keys_reply.content)
    jwks.extend(keys['keys'])
    google_uri = app.config["GOOGLE_KEYS_URI"]
    keys_reply = requests.get(google_uri)
    keys = json.loads(keys_reply.content)
    jwks.extend(keys['keys'])
    # print(jwks)

