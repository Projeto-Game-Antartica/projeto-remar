package br.ufscar.sead.loa.remar.saml

import grails.plugin.springsecurity.SpringSecurityService
import br.ufscar.sead.loa.remar.User
/**
 * Created by hugo on 11/05/17.
 */
class SamlSecurityService extends SpringSecurityService {

    static transactional = false
    def config

    Object getCurrentUser() {
        def userDetails
        if (!isLoggedIn()) {
            userDetails = null
        } else {
            userDetails = getAuthentication().details

            println (userDetails.class) // class org.springframework.security.web.authentication.WebAuthenticationDetails
            if ( config?.saml.autoCreate.active ) {
                userDetails =  getCurrentPersistedUser(userDetails)
            }
        }
        return userDetails
    }

    private Object getCurrentPersistedUser(userDetails) {
        if (userDetails) {
            String className = config?.userLookup.userDomainClassName
            String userKey = config?.saml.autoCreate.key

            println className
            println userKey

            if (className && userKey) {
                Class<?> userClass = grailsApplication.getDomainClass(className)?.clazz
                println userClass
                println userClass."findBy${userKey.capitalize()}"(userDetails."$userKey")
                return ((User) userClass."findBy${userKey.capitalize()}"(userDetails."$userKey"))
            }
        } else { return null}
    }
}
