package openidconnect

import grails.plugin.springsecurity.annotation.Secured

class LoginController {

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def auth() {
        def map = [book: "eikel"]
        render(view: "login", model: map)
    }

}
