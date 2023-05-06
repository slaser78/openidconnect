package openidconnect

import grails.core.GrailsApplication
import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonSlurper
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.springframework.beans.factory.annotation.Value
import groovy.json.JsonOutput
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate

class OpenIDKeycloakController {

    GrailsApplication grailsApplication
    OpenIDService openIDService
    UserService userService

//    Microsoft constants/secrets
    @Value('${rm.openid.keycloak.clientid}')
    String client_id
    @Value('${rm.openid.keycloak.redirect_uri}')
    String redirect_uri
    @Value('${rm.openid.keycloak.scope}')
    String scope
    @Value('${rm.openid.keycloak.clientsecret}')
    String client_secret
    @Value('http://localhost:8080')
    String host
    @Value('/token')
    String token
    @Value('grant_type')
    String grant_type
    @Value('realm')
    String realm

    @Secured(value = ["hasRole('ROLE_ANONYMOUS')"])
    def kc_oauth2() {
        // calculate the Keycloak OpenID Connect authorization URL.
        withForm {
            String url = """
        http://localhost:8080/realms/CvmtRealm/protocol/openid-connect/auth
        ?response_type=code
        &client_id=$client_id
        """.stripIndent().replaceAll("[\r\n]+", "")
            println("URL: " + url)
            redirect(url: url)
        }.invalidToken {
            // something went wrong?
            redirect(controller: "login", action: "auth")
        }
    }

    @Secured(value = ["hasRole('ROLE_ANONYMOUS')"])
    def kc_oauth2callback() {
        String auth_code = request.getParameter('code')
        if (!auth_code) {
            throw new IllegalAccessException("No code parameter in OpenID Connect callback")
        }
        Map<String, String> data = [
                "username"     : "scott",
                "password"     : "abcd",
                "client_id"    : client_id,
                "client_secret": client_secret,
                "grant_type"   : "password",
                "scope"        : "openid"
        ]
        String body = JsonOutput.toJson(data)
        println "Body: " + body
        String uri = "http://localhost:8080/realms/CvmtRealm/protocol/openid-connect/token"
        SSLContext sslContext = getSslContext()
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build()
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager(registry))
                .build()
        try {
            HttpPost httpPost = new HttpPost(uri)
            httpPost.addHeader("Content-Type", "application/json")
            StringEntity entity = new StringEntity(body)
            httpPost.setEntity(entity)
            CloseableHttpResponse response = httpClient.execute(httpPost)
            try {
                if (response.getStatusLine().getStatusCode() == 200) {
                    log.warn("HTTP Post worked.")
                } else {
                    log.error("Did not retrieve token.")
                }
            } catch (e) {
                log.error(e.getMessage())
                log.error("Response for retrieving token failed")
            } finally {
                response.close()
            }
            def map = new JsonSlurper().parseText(resp.body())
            String id_token = map["id_token"]
            String token = openIDService.verify("keycloak", id_token)
            if (!token) {
                throw new IllegalAccessException("Illegal code parameter in OpenID Connect callback")
            }
            userService.loginByToken(token)
        } catch (e) {
            log.error("Error retrieving token: " + e.getMessage())
        } finally {
            httpClient.close()
        }
        redirect(controller: "Content", action: "index")
    }

    def getSslContext() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    X509Certificate[] getAcceptedIssuers() {
                        return null
                    }

                    void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }

                    void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }
        }
        SSLContext sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, new SecureRandom())
        return sslContext
    }
}