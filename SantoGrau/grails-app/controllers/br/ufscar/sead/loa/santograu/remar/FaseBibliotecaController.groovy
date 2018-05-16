package br.ufscar.sead.loa.santograu.remar

import grails.plugin.springsecurity.annotation.Secured
import static org.springframework.http.HttpStatus.*
import br.ufscar.sead.loa.remar.api.MongoHelper
import grails.transaction.Transactional
import grails.util.Environment
import groovy.json.JsonSlurper

@Secured(["isAuthenticated()"])
class FaseBibliotecaController {

    def springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", exportLevel: "POST"]

    def beforeInterceptor = [action: this.&check, only: ['index', 'exportLevel']]


    private check() {
        if (springSecurityService.isLoggedIn())
            session.user = springSecurityService.currentUser
        else {
            log.debug "Logout: session.user is NULL !"
            session.user = null
            redirect controller: "login", action: "index"

            return false
        }
    }

    @Secured(['permitAll'])
    def index(Integer max) {
        if (params.t) {
            session.taskId = params.t
        }

        if (params.p) {
            session.processId = params.p
        }
        respond new FaseBiblioteca(params)
    }

    def show(FaseBiblioteca faseBibliotecaInstance) {
        respond faseBibliotecaInstance
    }

    def create() {
        respond new FaseBiblioteca(params)
    }

    @Transactional
    def save(FaseBiblioteca faseBibliotecaInstance) {
        if (faseBibliotecaInstance == null) {
            notFound()
            return
        }

        if (faseBibliotecaInstance.hasErrors()) {
            respond faseBibliotecaInstance.errors, view:'create'
            return
        }

        faseBibliotecaInstance.palavra1[0]= params.palavra1dica1
        faseBibliotecaInstance.palavra1[1]= params.palavra1dica2
        faseBibliotecaInstance.palavra1[2]= params.palavra1dica3
        faseBibliotecaInstance.palavra1[3]= params.palavra1
        faseBibliotecaInstance.palavra2[0]= params.palavra2dica1
        faseBibliotecaInstance.palavra2[1]= params.palavra2dica2
        faseBibliotecaInstance.palavra2[2]= params.palavra2dica3
        faseBibliotecaInstance.palavra2[3]= params.palavra2
        faseBibliotecaInstance.palavra3[0]= params.palavra3dica1
        faseBibliotecaInstance.palavra3[1]= params.palavra3dica2
        faseBibliotecaInstance.palavra3[2]= params.palavra3dica3
        faseBibliotecaInstance.palavra3[3]= params.palavra3
        faseBibliotecaInstance.ownerId = session.user.id as long
        faseBibliotecaInstance.taskId = session.taskId as String

        faseTecnologiaInstance.save flush:true
    }

    def edit(FaseBiblioteca faseBibliotecaInstance) {
        respond faseBibliotecaInstance
    }

    //@Transactional
    def update(FaseBiblioteca faseBibliotecaInstance) {
        if (faseBibliotecaInstance == null) {
            notFound()
            return
        }

        if (faseBibliotecaInstance.hasErrors()) {
            respond faseBibliotecaInstance.errors, view:'edit'
            return
        }

        faseBibliotecaInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'FaseBiblioteca.label', default: 'FaseBiblioteca'), faseBibliotecaInstance.id])
                redirect faseBibliotecaInstance
            }
            '*'{ respond faseBibliotecaInstance, [status: OK] }
        }
    }

    //@Transactional
    def delete(FaseBiblioteca faseBibliotecaInstance) {

        if (faseBibliotecaInstance == null) {
            notFound()
            return
        }

        faseBibliotecaInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'FaseBiblioteca.label', default: 'FaseBiblioteca'), faseBibliotecaInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'faseBiblioteca.label', default: 'FaseBiblioteca'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    @Secured(['permitAll'])
    def exportLevel(){

        //cria a instancia da fase tecnologia com os valores digitados pelo usuario
        FaseBiblioteca faseBiblioteca = new FaseBiblioteca()
        faseBiblioteca.palavra1[0] = params.palavra1dica1
        faseBiblioteca.palavra1[1] = params.palavra1dica2
        faseBiblioteca.palavra1[2] = params.palavra1dica3
        faseBiblioteca.palavra1[3] = params.palavra1
        faseBiblioteca.palavra2[0] = params.palavra2dica1
        faseBiblioteca.palavra2[1] = params.palavra2dica2
        faseBiblioteca.palavra2[2] = params.palavra2dica3
        faseBiblioteca.palavra2[3] = params.palavra2
        faseBiblioteca.palavra3[0] = params.palavra3dica1
        faseBiblioteca.palavra3[1] = params.palavra3dica2
        faseBiblioteca.palavra3[2] = params.palavra3dica3
        faseBiblioteca.palavra3[3] = params.palavra3

        //cria o arquivo json da fase
        createJsonFile("biblioteca.json", faseBiblioteca)

        // Finds the created file path
        def folder = servletContext.getRealPath("/data/${springSecurityService.currentUser.id}/${session.taskId}")
        def id = MongoHelper.putFile("${folder}/biblioteca.json")

        def port = request.serverPort
        if (Environment.current == Environment.DEVELOPMENT) {
            port = 8080
        }
        // Updates current task to 'completed' status
        render  "http://${request.serverName}:${port}/process/task/complete/${session.taskId}?files=${id}"

    }

    void createJsonFile(String fileName, FaseBiblioteca faseBiblioteca){
        def dataPath = servletContext.getRealPath("/data")
        def instancePath = new File("${dataPath}/${springSecurityService.currentUser.id}/${session.taskId}")

        instancePath.mkdirs()

        File file = new File("$instancePath/"+fileName);
        def pw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))
        pw.write("{\n");
        pw.write("\t\"dicas1\": [\""
                + faseBiblioteca.palavra1[0].replace("\"","\\\"") + "\", \""
                + faseBiblioteca.palavra1[1].replace("\"","\\\"") + "\", \""
                + faseBiblioteca.palavra1[2].replace("\"","\\\"") + "\""
                + "],\n"
                + "\t\"dicas2\": [\""
                + faseBiblioteca.palavra2[0].replace("\"","\\\"") + "\", \""
                + faseBiblioteca.palavra2[1].replace("\"","\\\"") + "\", \""
                + faseBiblioteca.palavra2[2].replace("\"","\\\"") + "\""
                + "],\n"
                + "\t\"dicas3\": [\""
                + faseBiblioteca.palavra3[0].replace("\"","\\\"") + "\", \""
                + faseBiblioteca.palavra3[1].replace("\"","\\\"") + "\", \""
                + faseBiblioteca.palavra3[2].replace("\"","\\\"") + "\""
                + "],\n"
                + "\t\"respostas\": [\""
                + faseBiblioteca.palavra1[3].replace("\"","\\\"") + "\", \""
                + faseBiblioteca.palavra2[3].replace("\"","\\\"") + "\", \""
                + faseBiblioteca.palavra3[3].replace("\"","\\\"") + "\""
                + "]\n")
        pw.write("}")
        pw.close()
    }

}