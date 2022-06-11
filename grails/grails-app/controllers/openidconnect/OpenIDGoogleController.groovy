package openidconnect

import grails.core.GrailsApplication
import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonSlurper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import org.springframework.beans.factory.annotation.Value

class OpenIDGoogleController {

    GrailsApplication grailsApplication
    OpenIDService openIDService
    UserService userService

//    Microsoft constants/secrets
    @Value('${rm.openid.google.clientid}')
    String client_id
    @Value('${rm.openid.google.redirect_uri}')
    String redirect_uri
    @Value('${rm.openid.google.scope}')
    String scope
    @Value('${rm.openid.google.clientsecret}')
    String client_secret
    @Value('https://oauth2.googleapis.com/')
    String host
    @Value('/token')
    String url
    @Value('authorization_code')
    String grant_type

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def google_oauth2() {
        // calculate the (complex) google OpenID Connect authorisation URL.
        withForm {
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
            redirect(controller: "openIDGoogle", action: "index", params: params)
        }
    }

    @Secured(value=["hasRole('ROLE_ANONYMOUS')"])
    def google_oauth2callback() {
        String auth_code = request.getParameter('code')
        if(!auth_code) {
            throw new IllegalAccessException("No code parameter in OpenID Connect callback")
        }
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
        if(!token) {
            throw new IllegalAccessException("Illegal code parameter in OpenID Connect callback")
        }

        userService.loginByToken(token)

        redirect(controller: "Content", action: "index")
    }
}