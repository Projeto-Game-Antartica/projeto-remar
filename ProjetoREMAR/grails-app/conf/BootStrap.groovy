import br.ufscar.sead.loa.remar.Role
import br.ufscar.sead.loa.remar.UserRole
import br.ufscar.sead.loa.remar.User

import javax.servlet.http.HttpServletRequest

class BootStrap {

    def init = { servletContext ->

        HttpServletRequest.metaClass.isXhr = {->
            'XMLHttpRequest' == delegate.getHeader('X-Requested-With')
        }

        def allRoles = Role.findAll()
        def found = allRoles.findAll {it.authority == "ROLE_ADMIN"}
        if (found == []) {
            def adminRole = new Role(authority: "ROLE_ADMIN").save flush: true
            println "ROLE_ADMIN inserted"
        }

        found = allRoles.findAll {it.authority == "ROLE_PROF"}
        if (found == []) {
            def profRole = new Role(authority: "ROLE_PROF").save flush: true
            println "ROLE_PROF inserted"
        }

        found = allRoles.findAll {it.authority == "ROLE_STUD"}
        if (found == []) {
            def studentRole = new Role(authority: "ROLE_STUD").save flush: true
            println "ROLE_STUD inserted"
        }

        found = allRoles.findAll {it.authority == "ROLE_EDITOR"}
        if (found == []) {
            def editorRole = new Role(authority: "ROLE_EDITOR").save flush: true
            println "ROLE_EDITOR inserted"
        }

        found = allRoles.findAll {it.authority == "ROLE_DESENVOLVEDOR"}
        if (found == []) {
            def devRole = new Role(authority: "ROLE_DESENVOLVEDOR").save flush: true
            println "ROLE_DESENVOLVEDOR inserted"
        }

        /*def admin = new User(
         
                username: "admin",
                password: "admin",
                firstName: "Cleyton",
                lastName: "Junior",
                enabled: true).save flush: true

        def professor = new User(
                firstName: "Cleyson",
                lastName: "Silva",
                username: "prof",
                password: "prof",
                enabled: true).save flush: true

        def student = new User(
                username: "stud",
                password: "stud",
                firstName: "Cleiton",
                lastName: "Souza",
                enabled: true).save flush: true

        if(admin.hasErrors()){
            println admin.errors
        }

        if(professor.hasErrors()){
            println professor.errors
        }

        if(student.hasErrors()){
            println student.errors
        }

        def adminRole = new Role(authority: "ROLE_ADMIN").save flush: true
        def professorRole = new Role(authority: "ROLE_PROF").save flush: true
        def studentRole = new Role(authority: "ROLE_STUD").save flush: true

        UserRole.create(admin, adminRole, true)
        UserRole.create(admin, professorRole, true)
        UserRole.create(admin, studentRole, true)
        UserRole.create(professor, professorRole, true)
        UserRole.create(student, studentRole, true)

//        def springContext = WebApplicationContextUtils.getWebApplicationContext(servletContext)
//        springContext.getBean('marshallers').register();

        println "Bootstrap: done"
*/

    }
    def destroy = {
    }
}
