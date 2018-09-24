package br.ufscar.sead.loa.remar

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class SamlSecurityService extends SpringSecurityService {

    static transactional = false
    def config

    Object getCurrentUser() {
        def userDetails
        if (!isLoggedIn()) {
            userDetails = null
        } else {
            def authType = getAuthentication()
            userDetails = getAuthentication().details
            if (authType instanceof UsernamePasswordAuthenticationToken) {
                String className = SpringSecurityUtils.securityConfig.userLookup.userDomainClassName
                String usernamePropName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName
                userDetails = grailsApplication.getClassForName(className).findWhere((usernamePropName): principal.username)
            } else {
                if ( config?.saml.autoCreate.active ) {
                    userDetails =  getCurrentPersistedUser(userDetails)
                }
            }

        }

        return userDetails
    }

    private Object getCurrentPersistedUser(userDetails) {
        if (userDetails) {
            String className = config?.userLookup.userDomainClassName
            String userKey = config?.saml.autoCreate.key
            if (className && userKey) {
                Class<?> userClass = grailsApplication.getDomainClass(className)?.clazz
                return userClass."findBy${userKey.capitalize()}"(userDetails."$userKey")
            }
        } else { return null}
    }
}
