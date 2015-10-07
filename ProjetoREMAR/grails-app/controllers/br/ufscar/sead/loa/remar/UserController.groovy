package br.ufscar.sead.loa.remar

//import com.daureos.facebook.FacebookGraphService
import grails.plugin.mail.MailService
import grails.util.Environment
import org.apache.commons.lang.RandomStringUtils
import org.camunda.bpm.engine.IdentityService
import org.springframework.web.multipart.commons.CommonsMultipartFile

import static org.springframework.http.HttpStatus.*

import grails.transaction.Transactional
import grails.plugins.rest.client.RestBuilder
import grails.converters.JSON


@Transactional(readOnly = true)
class UserController {
    def springSecurityService
    MailService mailService
//    FacebookGraphService facebookGraphService

    IdentityService identityService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", filteredList: "POST"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        respond User.list(params)
    }

    def test() {
        render '^~^'
    }

    def show(User userInstance) {
        respond userInstance, model:[userInstance: userInstance]
    }

    def create() {
        respond new User(params), model:[admin: false, stud: false, dev: false, source: "create"]
    }

    @Transactional(readOnly=false)
    def saveCamundaDB(userInstance){

//        org.camunda.bpm.engine.identity.User camundaUser = identityService.newUser(userInstance.username)
//        if(camundaUser.firstName == null) {
//            camundaUser.setEmail(userInstance.email)
//            camundaUser.setFirstName(userInstance.username)
//            camundaUser.setPassword(userInstance.password)
//            userInstance.camunda_id = camundaUser.getId()
//            identityService.saveUser(camundaUser)
//        }
//        else
//            redirect(controller: 'index')
    }
    @Transactional(readOnly=false)
    def confirmNewUser(){

        println params.Token
        if(EmailToken.findByToken(params.Token)){
            def userId = EmailToken.findByToken(params.Token).idOwner
            def currentNewUser = User.findById(userId)
            currentNewUser.accountLocked = false
            currentNewUser.enabled = true
  //          currentNewUser.save()
            saveCamundaDB(currentNewUser)
            println "Token correto - cadastro liberado"
            render(view: '/static/emailuser')
        }
        else
            render "deu errado! :("
    }

    def sendConfirmationMail(userEmail,userId){


        String charset =(('A') + ('0'..'9').join())
        Integer length = 9
        String randomString = RandomStringUtils.random(length, charset.toCharArray())
        def newToken = new EmailToken(token: randomString, idOwner: userId)
        newToken.save flush: true

        mailService.sendMail {
            async true
            to userEmail
            subject "Confirmação de Cadastro"
            html '<h3>Clique no link abaixo para confirmar o cadastro</h3> <br>' +
                    '<br>' +
                    "http://${request.serverName}:${request.serverPort}/user/email/confirm?Token=${newToken.getToken()}"
        }

        println "metodo do email"

    }
    @Transactional(readOnly=false)
    def newPassword(){
        // vai receber aqui a nova senha por post


        if(params.newPassword == params.confirmPassword){
            def user = User.findById(params.userid)
//            user.passwordExpired = false      NAO!
            user.password = params.confirmPassword
            user.accountLocked = false
            user.accountExpired = false
            println "password alterado"

        }


    }

    @Transactional(readOnly=false)
    def createPassword(){
        if(PasswordToken.findByToken(params.Token)){
            def userToChange = User.findById(PasswordToken.findByToken(params.Token).idOwner)
            respond("", model: [user: userToChange.id])

        }
        else{
            // renderizar pagina de erro, token errado
            render "token errado ou expirado"
        }
    }
    @Transactional(readOnly=false)
    def confirmEmail() {
        def userIP = request.getRemoteAddr()

        def captcha = params.get("g-recaptcha-response")

        def rest = new RestBuilder()

        def resp = rest.get("https://www.google.com/recaptcha/api/siteverify?secret=6LdA8QkTAAAAACHA9KoBPT1BXXBrJpQNJfCGTm9x&response=" + captcha + "&remoteip=" + userIP)
        println resp.json as JSON
        if (resp.json.success == true) {
            println params.email
            println User.findByEmail(params.email)
            if (User.findByEmail(params.email)) {
                println User.findByEmail(params.email)
                //User.findByEmail(params.email).passwordExpired = true  NAO!
                String charset = (('A') + ('0'..'9').join())
                Integer length = 9
                String randomString = RandomStringUtils.random(length, charset.toCharArray())
                def newToken = new PasswordToken(token: randomString, idOwner: User.findByEmail(params.email).getId())
                newToken.save flush: true

                mailService.sendMail {
                    async true
                    to params.email
                    subject "Nova senha para o REMAR"
                    html '<h3>Clique no link abaixo para fazer uma nova senha</h3> <br>' +
                            '<br>' +
                            "http://${request.serverName}:${request.serverPort}/user/newpassword/confirm?Token=${newToken.getToken()}"

                }

                render(view: "/static/emailsent")

            } else {
                // TODO EXIBIR FLASH MESSAGE DE EMAIL  NAO ENCONTRADO
                flash.message = message(code: 'error.mail')
                redirect(uri: '/password/reset')
            }

        }
    }

    @Transactional
    def save(User instance) {
        
        def userIP = request.getRemoteAddr()
        def recaptchaResponse = params.get("g-recaptcha-response")
        def rest = new RestBuilder()
        
        def resp = rest.get("https://www.google.com/recaptcha/api/siteverify?" +
                "secret=6LdA8QkTAAAAACHA9KoBPT1BXXBrJpQNJfCGTm9x&response=" + recaptchaResponse + "&remoteip=" + userIP)
        if(resp.json.success){
            if (instance == null) {
                notFound()
                return
            }

            if (instance.hasErrors()) { // TODO
                respond instance.errors, view:'create'
                println instance.errors
                return
            }

            def root = servletContext.getRealPath("/")
            def f = new File("${root}data/users/${instance.username}")
            f.mkdirs()
            def destination = new File(f, "profile-picture")
            def photo = params.photo as CommonsMultipartFile

            if(!photo.isEmpty()) {
                photo.transferTo(destination)
            } else {
                new AntBuilder().copy(file: "${root}images/avatars/${instance.gender}.png", tofile: destination)
            }

            if (Environment.current == Environment.DEVELOPMENT) {
                instance.accountExpired = false
                instance.accountLocked = false
                instance.enabled = true
                instance.passwordExpired = false

                org.camunda.bpm.engine.identity.User camundaUser = identityService.newUser(instance.username)
                camundaUser.setEmail(instance.email)
                camundaUser.setFirstName(instance.firstName)
                camundaUser.setLastName(instance.lastName)
                camundaUser.setPassword(instance.password)
                camundaUser.setId(instance.username)
                identityService.saveUser(camundaUser)

                instance.camunda_id = camundaUser.id
                instance.save flush: true
                sendConfirmationMail(instance.getEmail(), instance.getId())

            } else {
                instance.accountExpired = false
                instance.accountLocked = true //Before user confirmation
                instance.enabled = false        // Before user confirmation
                instance.passwordExpired = false
                instance.setGender(instance.gender) //teste

                org.camunda.bpm.engine.identity.User camundaUser = identityService.newUser(instance.username)
                camundaUser.setEmail(instance.email)
                camundaUser.setFirstName(instance.firstName)
                camundaUser.setLastName(instance.lastName)
                camundaUser.setPassword(instance.password)
                camundaUser.setId(instance.username)
                identityService.saveUser(camundaUser)

                instance.camunda_id = camundaUser.id
                instance.save flush: true
                sendConfirmationMail(instance.getEmail(), instance.getId())
            }

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), instance.id])
                    redirect instance
                }
                '*' { respond instance, [status: CREATED] }
            }
        }
        else{
//            flash.message = message(code: 'bla')
//            //flash.message = message("Clique no recpatcha")
//            redirect (controller: "user", action: "create")

            //VALIDACAO SENDO FEITA NO CLIENTE
        }

    }


    def edit(User userInstance) {
        respond userInstance, model:[admin: userInstance.isAdmin(), prof: userInstance.isProf(), stud: userInstance.isStud(), editor: userInstance.isEditor(), dev: userInstance.isDev(), source: "create", userInstance: userInstance]
    }

    @Transactional
    def update(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view:'edit'
            return
        }

        //Remove user from camunda to insert it again
        identityService.deleteUser(userInstance.camunda_id)

        //Remove user roles to insert it again
        UserRole.removeAll(userInstance, true)

        //reinsert the user in the camunda BD
        org.camunda.bpm.engine.identity.User camundaUser = identityService.newUser(userInstance.username)
        camundaUser.setEmail(userInstance.email)
        camundaUser.setFirstName(userInstance.firstName)
        camundaUser.setLastName(userInstance.lastName)
        camundaUser.setPassword(userInstance.password)
        identityService.saveUser(camundaUser)

        userInstance.camunda_id = camundaUser.getId()

        userInstance.save flush:true

        //Reinsert all the user roles
        if(params.ROLE_ADMIN) {
            UserRole.create(userInstance, Role.findByAuthority("ROLE_ADMIN"), true)
        }

        if(params.ROLE_PROF) {
            UserRole.create(userInstance, Role.findByAuthority("ROLE_PROF"), true)
        }

        if(params.ROLE_STUD) {
            UserRole.create(userInstance, Role.findByAuthority("ROLE_STUD"), true)
        }

        if(params.ROLE_EDITOR) {
            UserRole.create(userInstance, Role.findByAuthority("ROLE_EDITOR"), true)
        }

        if(params.ROLE_DESENVOLVEDOR) {
            UserRole.create(userInstance, Role.findByAuthority("ROLE_DESENVOLVEDOR"), true)
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect userInstance
            }
            '*'{ respond userInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(User userInstance) {

        if (userInstance == null) {
            notFound()
            return
        }

        UserRole.removeAll(userInstance, true)

        identityService.deleteUser(userInstance.camunda_id)

        userInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    @Transactional
    def makeDeveloper() {
        UserRole.create(springSecurityService.getCurrentUser() as User, Role.findByAuthority("ROLE_DEV"), true)
        println("Deu Certo")
        println(params.fullName)
        render(view:"/static/newDeveloper")

    }



    @Transactional
    def filteredList(String filter) {
        def query = "from User where camunda_id LIKE '%" + filter + "%' OR email LIKE '%" + filter + "%' OR username LIKE '%" + filter + "%' OR name LIKE '%" + filter + "%'"
        def list = User.executeQuery(query)

        render(template: "grid", model:[userInstanceList: list])
    }

    def usernameAvailable(){
        println params.username
        render User.findByUsername(params.username) == null
    }

    def autocomplete() {
        List<User> allUsers = User.getAll();
        String autocompleteAlternatives = "";
        if (params.autocomplete !="") {
            for (User users : allUsers) {
                if (users.getUsername().contains(params.autocomplete)) {
                    autocompleteAlternatives +=users.getUsername() + ",";
                }
            }
            println(autocompleteAlternatives)
            render autocompleteAlternatives
        }
    }

    def emailAvailable(){
        render User.findByEmail(params.email) == null
    }

}