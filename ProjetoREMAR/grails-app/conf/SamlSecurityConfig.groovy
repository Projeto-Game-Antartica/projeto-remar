security {
	saml {
		userAttributeMappings = ['firstName': 'urn:mace:dir:attribute-def:givenName',
								 'lastName': 'urn:mace:dir:attribute-def:sn',
								 'email': 'urn:mace:dir:attribute-def:mail']
		userGroupToRoleMapping = [:]
		active = true
		afterLoginUrl = '/'
		afterLogoutUrl = '/'
		loginFormUrl = '/saml/login'
		userGroupAttribute = "memberOf"
		responseSkew = 60
		idpSelectionPath = '/'
		autoCreate {
			active =  true
			key = 'username'
			assignAuthorities = true
		}
		metadata {
			defaultIdp = 'http://mock-idp'
			url = '/saml/metadata'
			//default idp info
			idp {
				file = 'security/idp.xml'
				alias = 'http://mock-idp'
			}
			sp {
				file = 'security/sp.xml'
				alias = 'remar'
				defaults{
					local = true
					alias = 'remar'
					signingKey = 'ping'
					encryptionKey = 'ping'
					tlsKey = 'ping'
					requireArtifactResolveSigned = false
					requireLogoutRequestSigned = false
					requireLogoutResponseSigned = false
					idpDiscoveryEnabled = true
				}
			}
		}
		keyManager {
			storeFile = 'classpath:security/keystore.jks'
			storePass = 'nalle123'
			passwords = [ ping: 'ping123' ]
			defaultKey = 'ping'
		}
	}
}
