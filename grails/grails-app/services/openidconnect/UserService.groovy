package openidconnect

class UserService {

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

}