# OpenID Connect Demonstration to local Keycloak instance

This repositories contains demonstration projects for various
web frameworks that use OpenID Connect 
authentication.

## Frameworks
* [Groovy/Grails](grails/docs/README.md)

## OpenID Connect
This is not an introduction to OpenID Connect, but the following steps are taken when a user
is logging in using OpenID Connect (this sequence uses keycloak):

1. The user is redirected to '/login' because access to a secured page is refused.
2. The user presses the 'keycloak' button.
3. The button triggers '/login/oauth2/code/keycloak/init' which redirects the browser to a 'keycloak' login page.
4. The user interacts with the keycloak login page and authenticates.
5. After successfull authentication, the keycloak page forwards the browser to the specified redirect page which triggers '/login/oauth2/code/keycloak/'
6. The triggered code checks if the returned token matches an existing user and creates a new user if it doesn't.
7. The triggered code then logs in the user and redirects the user to the original secured page.

Note that your application needs an SSL version of Keycloak.

## JSON Web Token (JWT) or id_token

The OpenID Connect provider inserts an id_token in the successfull authentication callback. This id_token
does not directly uniquely identifies a user. Instead, it needs to be decoded first.
During this decoding the correct public key of the OpenID Connect provider is needed plus some complex
cryptgraphical libraries. The collecting of keys and the calling of the cryptographical libaries is
done in OpenIDService.

When the id_token is successfully decoded and reveals a subject/user_token, we trust that subject and
find/create/login a user based on this trust.

## Registering with OpenID Connect Authentication Providers

### External configuration for OpenID

Several configuration variables are required to use an
OpenID Connect service. Some of these configuration variables
are confident and should kept secret (i.e. not in your GitHub repo). 

This file can be found in the project at grails-app/conf/openid.yml
rm:
    openid:
        keycloak:
            realm: CvmtRealm
            clientid: cvmtClientId
            clientsecret: zZZ2Sk51XvdVaMMF08CQWLwwC8BV9J6n
            keys_host: http://localhost:8080/realms/CvmtRealm/protocol/openid-connect/token
            redirect_uri: http://localhost:8081/content
            scope: openid
            grant_type: authorization_code

```
You get this secret for your application from the Keycloak realm/client_id instance.

Good luck!

