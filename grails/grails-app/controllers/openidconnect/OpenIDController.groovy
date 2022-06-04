package openidconnect

import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

class OpenIDController {

    GrailsApplication grailsApplication
    SpringSecurityService springSecurityService
    OpenIDService openIDService

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def ms_oauth2() {
        // calculate the (complex) microsoft OpenID Connect authorisation URL.
        withForm {
            String token = "token"
            String client_id = grailsApplication.config.getProperty('rm.openid.microsoft.clientid')
            String redirect_uri = grailsApplication.config.getProperty('rm.openid.microsoft.redirect_uri')
            String scope = grailsApplication.config.getProperty('rm.openid.microsoft.scope')
            String url = """
        https://login.microsoftonline.com/common/oauth2/v2.0/authorize
        ?response_type=id_token
        &response_mode=form_post
        &state=$token
        &nonce=robo_nonce
        &client_id=$client_id
        &redirect_uri=$redirect_uri
        &scope=$scope
        """.stripIndent().replaceAll("[\r\n]+", "")
            redirect(url: url)
        }.invalidToken {
            // something went wrong?
            redirect(controller: "openID", action: "index", params: params)
        }
    }

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def ms_oauth2callback() {
        // get the user/id_token from the microsoft answer and verify it into the unique user token.
        String id_token = params["id_token"]
        String token = openIDService.verify("ms", id_token)

        // find (and create if needed) the user belonging to this token
        User user = User.findByToken(token)
        if (user == null) {
            User.withTransaction { status ->
                user = new User(username: "user", password: "dummy", token: token)
                user.save(flush: true, failOnError: true)
            }
        }

        // login this user with spring-security
        springSecurityService.reauthenticate(user.username)

        redirect(controller: "Content", action: "index")
    }
}