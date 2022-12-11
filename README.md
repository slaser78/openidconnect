# OpenID Connect Demonstrations

This repositories contains demonstration projects for various
web frameworks that use OpenID Connect 
authentication.

## Frameworks
* [Groovy/Grails](grails/docs/README.md)
* [Python/Flask](python/docs/README.md)

## OpenID Connect
This is not an introduction to OpenID Connect, but the following steps are taken when a user
is logging in using OpenID Connect (this sequence uses microsoft):

1. The user is redirected to a page like '/login' because access to a secured page is refused.
2. The user presses (for example) the 'microsoft' button.
3. The button triggers (for example) '/login/oauth2/code/microsoft/init' which redirects the browser to a 'microsoft' login page.
4. The user interact with the microsoft login page and authenticates.
5. After successfull authentication, the microsoft page forwards the browser to a local page which triggers (for example) '/login/oauth2/code/microsoft/'
6. The triggered code checks if the returned token matches an existing User and create a new User if it doesn't.
7. The triggered code then logs the Userand forwards the browser to the original secured page.

Note that your application needs to be registered with the authentication provider (microsoft/google/github/...).

## JSON Web Token (JWT) or id_token

The OpenID Connect provider inserts an id_token in the successfull authentication callback. This id_token
does not directly uniquely identifies a user. Instead, it needs to be decoded first.
During this decoding the correct public key of the OpenID Connect provider is needed plus some complex
cryptgraphical libraries. The collecting of keys and the calling of the cryptographical libaries is
done in OpenIDService.

When the id_token is successfully decoded and turns up a subject/user_token we trust that subject and
find/create/login a user based on this trust.

## Registering with OpenID Connect Authentication Providers

### External configuration for OpenID

Several configuration variables are required to use an
OpenID Connect service such as that from microsoft. Some of these configuration variables
are confident and should kept secret (i.e. not in your GitHub repo). 

This file has a content like:
```yaml
rm:
    openid:
        microsoft:
            clientid: `secret11`
            redirect_uri: https%3A%2F%2Flocalhost%3A8888%2Flogin%2Foauth2%2Fcode%2Fmicrosoft
            scope: openid
            keys_host: https://login.microsoftonline.com/
            keys_uri: /common/discovery/v2.0/keys
            keys_url: https://login.microsoftonline.com/common/discovery/v2.0/keys
            issuer: https://login.microsoftonline.com/`secret12`/v2.0
            audience: `secret13`
```
You get these secret for your application by registering with the provider. For Microsoft use the following location:

* https://portal.azure.com
* App Services
* Manage Azure Active Directory
* App Registrations
* New Registration

For creating the credentials in the Google Cloud go to:

* https://console.cloud.google.com
* APIs & Services
* Credentials

Good luck!

