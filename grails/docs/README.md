# Grails/Groovy and OpenID Connect

## Overview

For our Grails OpenID Connect demonstration project we need add-on libraries that provides the following functionality:

1. User/Session representation and page protection (Spring-security)
2. Encryption support (java-jwt and kwls-rsa)
3. Outgoing HTTP calls (micronaut-http-client)

The rest of this document describes how to create a working Grails OpenID Connect demonstration project from scratch. 

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
and which was removed from this demonstration app.
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

The OpenIDController contains the methods that are called the button is pressed (ms_oauth2) and the method
that is called when microsoft indicates the user has been authenticated (ms_oauth2callback).

Grails allows you to store the required configuration variables in an external configuration file that is pointed 
to by the environment variable SPRING_CONFIG_ADDITIONAL_LOCATION, for example:
```bash
SPRING_CONFIG_ADDITIONAL_LOCATION=/etc/openidconnect/openidconnect.yml
```
An example of such a file (without the secrets) are [here](./openidconnect.yml).

## Connecting OpenID Connect to Grails Spring Security

After the call to the OpenID Connect provider (here microsoft or google) and a successfull 
authentication the provider calls back with something that after a verification is turned 
into a token. This token uniquely represents one authenticated user. 
The token is stored in a User object that is used by grails-spring-security to represents 
the user in this session. This logic is implemented by the method OpenIDController.loginByToken(token).



