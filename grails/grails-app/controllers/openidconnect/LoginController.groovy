package openidconnect

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

class LoginController {

    SpringSecurityService springSecurityService

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def auth() {
        def map = []
        render(view: "login", model: map)
    }

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def denied() {
        def map = []
        render(view: "denied", model: map)
    }

}
