package openidconnect

import grails.core.GrailsApplication
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.beans.factory.annotation.Value

class OpenIDMicrosoftController {

    GrailsApplication grailsApplication
    OpenIDService openIDService
    UserService userService

    @Value('token')
    String token
    @Value('${rm.openid.microsoft.clientid}')
    String client_id
    @Value('${rm.openid.microsoft.redirect_uri}')
    String redirect_uri
    @Value('${rm.openid.microsoft.scope}')
    String scope

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def ms_oauth2() {
        // calculate the (complex) microsoft OpenID Connect authorisation URL.
        withForm {
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
            redirect(controller: "openIDMicrosoft", action: "index", params: params)
        }
    }

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def ms_oauth2callback() {
        // get the user/id_token from the microsoft answer and verify it into the unique user token.
        String id_token = params["id_token"]
        if(!id_token) {
            throw new IllegalAccessException("Illegal id_token parameter in OpenID Connect callback")
        }
        String token = openIDService.verify("ms", id_token)
        if(!token) {
            throw new IllegalAccessException("Illegal token parameter in OpenID Connect callback")
        }
        userService.loginByToken(token)
        redirect(controller: "Content", action: "index")
    }
}