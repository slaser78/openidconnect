package openidconnect

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

class ContentController {
    SpringSecurityService springSecurityService

    @Secured(value=["hasRole('ROLE_USER')"])
    def index() {
        def username = "unknown"
        if(springSecurityService.principal) {
            username = springSecurityService.principal.username
        }
        def map = [
                username: username
        ]
        render(view: "index", model: map)
    }
}
