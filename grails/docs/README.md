# OpenID Connect: Create a demo OpenID Connect Grails application

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





## External configuration

Set environment variable:

SPRING_CONFIG_LOCATION=/etc/openidconnect

In the directory '/etc/openidconnect/' there should be a file application.yml.