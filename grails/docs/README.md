# OpenID Connect: Create a demo OpenID Connect Grails application


## Create a Basic Grails App
Start with an empty grails 5 project. Create it frome the commandline like this:
```bash
$ grails create-app openidconnect
```
Although this project depends on as little external plugins as possible we need a few, 'spring-security' 
as a security framework and java-jwt/jwks-rsa as crypto libraries and micronaut to perform REST calls . 
Include them in 'build.gradle' as follows:
```gradle
    implementation 'org.grails.plugins:spring-security-core:5.0.0'
    implementation 'com.auth0:java-jwt:3.19.2'
    implementation 'com.auth0:jwks-rsa:0.21.1'
    implementation 'io.micronaut:micronaut-http-client'
```
Next define some pages explicitely with controllers and views in 'grails-app/controllers/openidconnect/UrlMappings.groovy':
```groovy
class UrlMappings {
    static mappings = {
        "/"(controller: "Content", view:"index")
        "/login/auth"(controller: "Login", view:"auth")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
```
Checkout the source for the Controllers, Views and the main.gsp layout. All trivially simple so not to 
distract from the main attraction, OpenID Connect.

## Add Spring Security objects

Typically the basic Model classes and other Spring/Grails glue is added to your project using:
```bash
$ grails s2-quickstart openidconnect User Role
```
This will add the Model objects in 'grails-app/domain/openidconnect/*.groovy'.
It will however also use password and configuration stuff that is not used in this project
and which was removed for this demonstration app.
In the end, after modifying the modifications of 's2-quickstart', only 'Role.groovy', 'User.groovy', 'UserRole.groovy' 
were added and the following lines were added to 'conf/application.yml':

```yaml
grails:
    plugin:
        springsecurity:
            securityConfigType: "Annotation"
        userLookup:
            userDomainClassName:  'openidconnect.User'
            authorityJoinClassName: 'openidconnect.UserRole'
        authority:
            className: 'openidconnect.Role'
```
After this the application can be run but access to page /content/index is refused since there is no User to
login. We will change that by adding OpenID Connect logic to /login/auth.

## Add OpenID Connect support
This is not an introduction to OpenID Connect, but the following steps are taken when a user
is logging in using OpenID Connect:

1. The user is redirected to page '/login/auth' because access to a page is refused.
2. The user presses (for example) the 'microsoft' button.
3. The button triggers '/login/oauth2/code/microsoft/init' which redirects the browser to a 'microsoft' login page. 
4. The user interact with the microsoft login page and authenticates.
5. After successfull authentication, the microsoft page forwards the browser to local '/login/oauth2/code/microsoft/'.
6. This local page checks if the returned token matches an existing User and create a new User if it doesn't.
7. The local page then logs the User in with spring-security and forwards the browser to a secured page.

The OpenIDController contains the methods that are called the button is pressed (ms_oauth2) and the method
that is called when microsoft indicates the user has been authenticated (ms_oauth2callback).

## External configuration for OpenID

As can be seen in the OpenIDController several configuration variables are required to use an
OpenID Connect service such as that from microsoft. Some of these configuration variables 
are confident and should not be stored in the main configuration file. Grails allows you 
to store such variables in an external configuration file that is pointed to by the
environment variable SPRING_CONFIG_ADDITIONAL_LOCATION, for example:
```bash
SPRING_CONFIG_ADDITIONAL_LOCATION=/etc/openidconnect/openidconnect.yml
```
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
You get these secret for your application by registering it at microsoft in the following location:

* https://portal.azure.com
* App Services
* Manage Azure Active Directory
* App Registrations
* New Registration

Good luck!

## Connecting OpenID Connect to Grails Spring Security

After the call to the OpenID Connect provider (here microsoft or google) and a successfull 
authentication the provider calls back with something that after a verification is turned 
into a token. This token uniquely represents one authenticated user. 
The token is stored in a User object that is used by grails-spring-security to represents 
the user in this session. This logic is implemented by the method OpenIDController.loginByToken(token).



