package br.ufscar.sead.loa.remar

import org.springframework.security.core.GrantedAuthority

class User {

    static hasMany = [userGroups: UserGroup]

    transient springSecurityService

    String username
    String password
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    String email
    String firstName
    String lastName
    String facebookId
    String moodleUsername
    boolean firstAccess


    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true, nullable: false
        password blank: false, nullable: false
        firstName blank: false
        lastName blank: true
        email blank: false, email: true
        facebookId nullable: true
        moodleUsername nullable: true
        firstAccess blank: true, nullable: true
    }

    static mapping = {
        password column: '`password`'

        tablePerHierarchy false
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role }
    }

    String getName() {
        return firstName + " " + lastName
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    HashSet<GrantedAuthority> authoritiesHashSet() {
        def roles = UserRole.findAllByUser(this).collect { it.role }
        def auths = new HashSet<GrantedAuthority>()
        roles.each { role ->
            def auth = new GrantedAuthority() {

                @Override
                String getAuthority() {
                    return role.authority
                }
            }
            auths.add(auth)

        } as Set<Role>
        return auths
    }

}
