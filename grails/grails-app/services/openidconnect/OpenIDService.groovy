package openidconnect

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkException
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import grails.core.GrailsApplication
import groovy.json.JsonSlurper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient

import java.security.interfaces.RSAPublicKey

class OpenIDService {

    GrailsApplication grailsApplication
    def public_keys = [:]
    Map<String, JwkProvider> jwk_providers = [:]
    Map<String,String> issuers = [:]

    // this is called from Bootstrap
    void bootstrapInitialize() {
        // get the keycloak keys
        try {
            BlockingHttpClient client = HttpClient.create(host.toURL()).toBlocking()
            HttpRequest request = HttpRequest.GET(uri)
            HttpResponse<String> resp = client.exchange(request, String)
            client.close()
            def keys = new JsonSlurper().parseText(resp.body())
        } catch(Exception ex) {
            log.error (ex.getMessage())
        }
    }

    String verify(String type, String id_token) {
        try {
            final DecodedJWT decodedJWT = JWT.decode(id_token)
            final JwkProvider provider = jwk_providers[type]
            final Jwk jwk = provider.get(decodedJWT.getKeyId())
            final Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null)

            final JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuers[type])
                    .build()
            final DecodedJWT verifiedJWT = verifier.verify(id_token)
            return verifiedJWT.getClaim("sub").asString()
        } catch (JWTVerificationException | JwkException | MalformedURLException exception) {
            log.error (exception.getMessage())
            return null
        }
    }
}