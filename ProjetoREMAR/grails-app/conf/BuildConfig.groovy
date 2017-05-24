grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.server.port.http = 8080



    grails.project.fork = [
        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
        //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

        // configure settings for the test-app JVM, uses the daemon by default
        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
        // configure settings for the run-app JVM
        run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the run-war JVM
        war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the Console UI JVM
        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        mavenRepo "http://repo.spring.io/milestone/"
    }

    dependencies {
        runtime 'mysql:mysql-connector-java:5.1.29'
        // test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
        compile 'org.apache.ant:ant:1.9.6'
        compile 'org.apache.ant:ant-launcher:1.9.6'
        compile 'org.mongodb:mongodb-driver:3.2.1'
        compile 'org.mongodb:bson:3.0.4'



        compile('commons-httpclient:commons-httpclient:3.1') {
            excludes 'commons-codec', 'commons-logging', 'junit'
        }

        compile('ca.juliusdavies:not-yet-commons-ssl:0.3.9') {
            excludes 'commons-httpclient', 'log4j'
        }

        compile('org.opensaml:opensaml:2.6.1') {
            excludes 'commons-codec', 'commons-collections', 'commons-lang', 'esapi', 'jcip-annotations', 'jcl-over-slf4j', 'joda-time', 'jul-to-slf4j', 'junit', 'log4j-over-slf4j', 'logback-classic', 'openws', 'serializer', 'servlet-api', 'slf4j-api', 'spring-core', 'spring-mock', 'testng', 'velocity', 'xalan', 'xercesImpl', 'xml-apis', 'xml-resolver', 'xmlunit'
        }

        compile('org.opensaml:xmltooling:1.3.4') {
            excludes 'bcprov-jdk15', 'commons-codec', 'jcip-annotations', 'jcl-over-slf4j', 'joda-time', 'jul-to-slf4j', 'junit', 'log4j-over-slf4j', 'logback-classic', 'not-yet-commons-ssl', 'serializer', 'slf4j-api', 'testng', 'xalan', 'xercesImpl', 'xml-apis', 'xml-resolver', 'xmlsec', 'xmlunit'
        }

        compile('org.apache.velocity:velocity:1.7') {
            excludes 'ant', 'commons-collections', 'commons-lang', 'commons-logging', 'hsqldb', 'jdom', 'junit', 'log4j', 'logkit', 'oro', 'servlet-api', 'werken-xpath'
        }

        compile 'joda-time:joda-time:1.6.2'

        compile('org.opensaml:openws:1.4.4') {
            excludes 'commons-codec', 'commons-httpclient', 'jcip-annotations', 'jcl-over-slf4j', 'joda-time', 'jul-to-slf4j', 'junit', 'log4j-over-slf4j', 'logback-classic', 'serializer', 'servlet-api', 'slf4j-api', 'spring-core', 'spring-mock', 'testng', 'xalan', 'xercesImpl', 'xml-apis', 'xml-resolver', 'xmltooling', 'xmlunit'
        }

        compile 'org.bouncycastle:bcprov-jdk15:1.45'

        compile 'org.apache.santuario:xmlsec:1.4.4'

        compile('org.owasp.esapi:esapi:2.0.1') {
            excludes 'antisamy', 'bsh-core', 'commons-beanutils-core', 'commons-collections', 'commons-configuration', 'commons-fileupload', 'commons-io', 'jsp-api', 'junit', 'log4j', 'servlet-api', 'xom'
        }

        compile 'commons-collections:commons-collections:3.2.1'

        compile('org.springframework.security.extensions:spring-security-saml2-core:1.0.0.RC2') {
            excludes 'spring-security-core'
            excludes 'spring-security-web'
        }
    }

    plugins {
        // plugins for the build system only
        build ":tomcat:7.0.55"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        //compile ':cache:1.1.8'
        compile ":asset-pipeline:1.9.9"
        //compile ':spring-security-core:2.0-RC5'
        compile "org.grails.plugins:spring-security-core:2.0.0"

        compile ":quartz:1.0.2"
        compile ":recaptcha:1.2.0"
        compile ":rest-client-builder:2.0.0"
        compile "org.grails.plugins:rest:0.8"

        // plugins needed at runtime but not for compilation
        runtime ":hibernate4:4.3.5.5"
        runtime ":database-migration:1.4.0"
    }
}
