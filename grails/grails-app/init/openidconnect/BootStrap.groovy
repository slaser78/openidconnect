package openidconnect

import grails.core.GrailsApplication

class BootStrap {
    OpenIDService openIDService
    GrailsApplication grailsApplication

    def init = { servletContext ->
        // test get a propertie
        def dummy0 = grailsApplication.config.getProperty('grails.plugin.userLookup.userDomainClassName')
//        def dummy1 = grailsApplication.config.getProperty('grails.plugin.springsecurity.securityConfigType')
//        def dummy2 = grailsApplication.config.getProperty('grails.serverURL')
//        def dummy3 = grailsApplication.config.getProperty('rm.openid.microsoft.scope')
//        // test user creation
//        User.withTransaction {status ->
//            def user = new User(username: "root", token:"secret")
//            user.save(flush: true, failOnError: true)
//
//            def users = User.getAll()
//            print(users)
//        }

        openIDService.bootstrapInitialize()

    }

    def destroy = {
    }
}
