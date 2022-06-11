package openidconnect

import grails.plugin.springsecurity.SpringSecurityService

class UserService {

    SpringSecurityService springSecurityService

    // this is called from Bootstrap
    void bootstrapInitialize() {
        User.withTransaction {status ->
            Role userRole = Role.findByAuthority("ROLE_USER")
            if(! userRole) {
                userRole = new Role(authority: "ROLE_USER")
                userRole.save(flush:true, failOnError: true)
            }
        }
    }

    User findUser(String token) {
        return User.findByToken(token)
    }

    User createUser(String token) {
        User.withTransaction {status ->
            User user = User.findByToken(token)
            if(!user) {
                String userName = "user" + User.count()
                String password = UUID.randomUUID().toString()
                Role userRole = Role.findByAuthority("ROLE_USER")

                user = new User(username: userName, password: password, token:token)
                user.save(flush: true, failOnError: true)
                UserRole.create(user, userRole, true)
            }
            return user
        }
    }


    void loginByToken(String token) {
        // find (and create if needed) the user belonging to this token
        User user = findUser(token)
        if(!user) {
            user = createUser(token)
        }
        // login this user with spring-security
        springSecurityService.reauthenticate(user.username)
    }


}