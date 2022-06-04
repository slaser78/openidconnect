package openidconnect

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

    private static final long serialVersionUID = 1

    String username
    String password
    String token

    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired


    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    static constraints = {
        password nullable: true, blank: true, password: true
        token nullable: true, blank: true, password: false
        username nullable: false, blank: false, unique: true
    }

    static mapping = {
        password column: '`password`'
    }


}
