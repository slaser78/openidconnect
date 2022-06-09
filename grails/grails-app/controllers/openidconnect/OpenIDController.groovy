package openidconnect

import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonSlurper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient

class OpenIDController {

    GrailsApplication grailsApplication
    SpringSecurityService springSecurityService
    OpenIDService openIDService
    UserService userService

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

        loginByToken(token)

        redirect(controller: "Content", action: "index")
    }

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def google_oauth2() {
        // calculate the (complex) google OpenID Connect authorisation URL.
        withForm {
            String client_id = grailsApplication.config.getProperty('rm.openid.google.clientid')
            String redirect_uri = grailsApplication.config.getProperty('rm.openid.google.redirect_uri')
            String scope = grailsApplication.config.getProperty('rm.openid.google.scope')
            String url = """
        https://accounts.google.com/o/oauth2/v2/auth
        ?response_type=code
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
    def google_oauth2callback() {
        String auth_code = request.getParameter('code')
        String client_id = grailsApplication.config.getProperty('rm.openid.google.clientid')
        String client_secret = grailsApplication.config.getProperty('rm.openid.google.clientsecret')
        String redirect_uri = grailsApplication.config.getProperty('rm.openid.google.redirect_uri')
        String host = "https://oauth2.googleapis.com/"
        String url = "/token"
        String grant_type = "authorization_code"
        Map<String,String> data = [
                "code": auth_code,
                "client_id": client_id,
                "client_secret": client_secret,
                "redirect_uri": redirect_uri,
                "grant_type": grant_type
        ]
        BlockingHttpClient client = HttpClient.create(host.toURL()).toBlocking()
        HttpRequest request = HttpRequest.POST(url, data)
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()
        def map = new JsonSlurper().parseText(resp.body())
        String id_token = map["id_token"]
        String token = openIDService.verify("google", id_token)

        loginByToken(token)

        redirect(controller: "Content", action: "index")
    }

    def loginByToken(String token) {
        // find (and create if needed) the user belonging to this token
        User user = userService.findUser(token)
        if(!user) {
            user = userService.createUser(token)
        }
        // login this user with spring-security
        springSecurityService.reauthenticate(user.username)
    }
}