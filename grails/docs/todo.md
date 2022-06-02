# Up OpenID Connect: Create a demo OpenID Connect Grails application

## ToDo

* + create an empty grails application
* * create some content
* find out how/when spring-security checks access to a page and make it explicit
* find out where the login page is defined and define your own
* find out how to create an account and create an account when required
* find out how to log a user in and to that at the appropriate time

## Workflow

## Goal:
* login with google and microsoft
* only show real page when logged in
* dedicated login page (only google/microsoft buttons)
* support 'remember me' (no login if remembered)


## External configuration

Set environment variable:

SPRING_CONFIG_LOCATION=/etc/openidconnect

In the directory '/etc/openidconnect/' there should be a file application.yml.