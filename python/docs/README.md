# Python/Flask and OpenID Connect

## Overview

For our Python/Flask OpenID Connect demostration project we need add-on libraries that provides the following functionality:
1. User/Session representation and page protection (Flask-Login)
2. Encryption support (pyjwt[crypto], pyOpenSSL)
3. SSL support (pyOpenSSL)
4. Outgoing HTTP calls (requests)

## Self signed certificates

Some of the OpenID Connect service providers require applications to use HTTPS connections. 
For HTTPS we need certificates so a [script](../bin/createcert) is included to generate
self-signed certificates.

## Python demonstration application

The python demonstration app is simple. Some key points:

* the requirements are defined in [requirement.txt](requirement.txt)
* the application is started in script [../bin/openidconnect][bin/openidconnect]
  . the CSRFProject middleware is included in the Flask app
  . the login_manager middleware is included in the Flask app
  . the internal FLASK webserver is started
* the logic for the supported OpenID service providers
  * [openidconnect/oauth2/microsoft.py](../openidconnect/oauth2/microsoft.py)
  * [openidconnect/oauth2/google.py](../openidconnect/oauth2/google.py)

## OpenID Connect configuration

Flask allows you to store the required configuration variables in an external configuration file that is pointed
to by the environment variable SPRING_CONFIG_ADDITIONAL_LOCATION, for example:
```python
    app.config.from_file(args.config, load=json.load)
```
An example of such a file (without the secrets) are [here](./openidconnect.json).
