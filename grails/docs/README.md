# OpenID Connect: Create a demo OpenID Connect Grails application


## Create a Basic Grails App
Start with an empty grails 5 project. Create it frome the commandline like this:
```bash
$ grails create-app openidconnect
```
Although this project depends on as little external plugins as possible we need two, 'spring-security' 
as a security framework and java-jwt as crypto library. Include them in 'build.gradle' as follows:
```gradle
    implementation 'org.grails.plugins:spring-security-core:5.0.0'
    implementation 'com.auth0:java-jwt:3.19.2'
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


## External configuration

Set environment variable:

SPRING_CONFIG_LOCATION=/etc/openidconnect

In the directory '/etc/openidconnect/' there should be a file application.yml.