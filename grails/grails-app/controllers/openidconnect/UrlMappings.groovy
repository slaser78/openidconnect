package openidconnect

class UrlMappings {
    static mappings = {

        //content
        "/"(controller: "Content", action: "index")
        "/content"(controller: "Content", action: "index")
        "/content/secret"(controller: "Content", action: "secret")

        // login page
        "/login/auth"(controller: "Login", action: "auth")
        "/login/denied"(controller: "Login", action: "denied")

        // logout action
        "/logout"(controller: "Login", action: "logout")

        //OpenID entry-points
        "/login/oauth2/code/keycloak/init"(controller: "OpenIDKeycloak", action: "kc_oauth2")
        "/login/oauth2/code/keycloak"(controller: "OpenIDKeycloak", action: "kc_oauth2callback")

        // support views
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
