package openidconnect

class UrlMappings {
    static mappings = {

        //content
        "/"(controller: "Content", action: "index")
        "/content"(controller: "Content", action: "index")

        // login page
        "/login/auth"(controller: "Login", action: "auth")
        "/login/denied"(controller: "Login", action: "denied")

        //OpenID entry-points
        "/login/oauth2/code/microsoft/init"(controller: "OpenID", action: "ms_oauth2")
        "/login/oauth2/code/microsoft"(controller: "OpenID", action: "ms_oauth2callback")
        "/login/oauth2/code/google/init"(controller: "OpenID", action: "google_oauth2")
        "/login/oauth2/code/google"(controller: "OpenID", action: "google_oauth2callback")

        // support views
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
