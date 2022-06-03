package openidconnect

import grails.plugin.springsecurity.annotation.Secured

class ContentController {

//    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    @Secured(value=["hasRole('ROLE_USER')"])
    def index() {
        def map = [book: "eikel"]
        render(view: "index", model: map)
    }
}
