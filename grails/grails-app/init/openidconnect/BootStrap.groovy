package openidconnect

class BootStrap {
    OpenIDService openIDService
    UserService userService

    def init = { servletContext ->
        openIDService.bootstrapInitialize()
        userService.bootstrapInitialize()
    }

    def destroy = {
    }
}
