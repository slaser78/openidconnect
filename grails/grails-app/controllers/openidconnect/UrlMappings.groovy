package openidconnect

class UrlMappings {
    static mappings = {
        "/"(controller: "Content", action: "index")
        "/login/auth"(controller: "Login", action: "auth")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
