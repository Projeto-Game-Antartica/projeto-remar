package br.ufscar.sead.loa.santograu.remar

import br.ufscar.sead.loa.remar.api.MongoHelper
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import grails.util.Environment

import org.springframework.web.multipart.MultipartFile
import static org.springframework.http.HttpStatus.*

@Secured(["isAuthenticated()"])
class FaseFutebolController {

    def springSecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "DELETE", returnInstance: "GET"]
    def beforeInterceptor = [action: this.&check, only: ['index', 'exportQuestions','save', 'update', 'delete']]

    private check() {
        if (springSecurityService.isLoggedIn())
            session.user = springSecurityService.currentUser
        else {
            log.debug("Logout: session.user is NULL !")
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
        session.user = springSecurityService.currentUser
        def list = FaseFutebol.findAllByOwnerId(session.user.id)
        if (list.size() == 0) {
            new FaseFutebol(title: "Desafio1", correctAnswer: "a", ownerId: session.user.id, taskId: session.taskId).save flush: true
            new FaseFutebol(title: "Desafio2", correctAnswer: "b", ownerId: session.user.id, taskId: session.taskId).save flush: true
            list = FaseFutebol.findAllByOwnerId(session.user.id)
        }
        respond list, model: [faseFutebolInstanceCount: list.size(), errorImportQuestions:params.errorImportQuestions]
    }

    def show(FaseFutebol faseFutebolInstance) {
        respond faseFutebolInstance
    }

    def create() {
        respond new FaseFutebol(params)
    }

    @Transactional
    def save(FaseFutebol faseFutebolInstance) {

        if (faseFutebolInstance == null) {
            notFound()
            return
        }

        //faseFutebolInstance.title= params.title
        faseFutebolInstance.correctAnswer= params.correctAnswer
        faseFutebolInstance.ownerId = session.user.id as long
        faseFutebolInstance.taskId = session.taskId as String
        faseFutebolInstance.save flush:true

        redirect(action: "index")

        /*if (faseFutebolInstance.hasErrors()) {
            respond faseFutebolInstance.errors, view: 'create'
            return
        }

        faseFutebolInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'faseFutebol.label', default: 'FaseFutebol'), faseFutebolInstance.id])
                redirect faseFutebolInstance
            }
            '*' { respond faseFutebolInstance, [status: CREATED] }*/
    }


    def edit(FaseFutebol faseFutebolInstance) {
        respond faseFutebolInstance
    }

    @Transactional
    def update() {
        /*if (faseFutebolInstance == null) {
            notFound()
            return
        }*/
        FaseFutebol faseFutebolInstance = FaseFutebol.findById(Integer.parseInt(params.faseFutebolID))
        faseFutebolInstance.title = params.title
        faseFutebolInstance.correctAnswer = params.correctAnswer
        faseFutebolInstance.ownerId = session.user.id as long
        faseFutebolInstance.taskId = session.taskId as String
        faseFutebolInstance.save flush:true
        redirect action: "index"

        /*if (faseFutebolInstance.hasErrors()) {
            respond faseFutebolInstance.errors, view: 'edit'
            return
        }

        faseFutebolInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'FaseFutebol.label', default: 'FaseFutebol'), faseFutebolInstance.id])
                redirect faseFutebolInstance
            }
            '*' { respond faseFutebolInstance, [status: OK] }
        }*/
    }

    @Transactional
    def delete(FaseFutebol faseFutebolInstance) {

        if (faseFutebolInstance == null) {
            notFound()
            return
        }

        faseFutebolInstance.delete flush: true
        render 'delete ok'
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'faseFutebol.label', default: 'FaseFutebol'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    @Secured(['permitAll'])
    def returnInstance(FaseFutebol faseFutebolInstance) {
        if (faseFutebolInstance == null) {
            //notFound()
            render "null"
        } else {
            render faseFutebolInstance.title + "%@!" +
                    faseFutebolInstance.correctAnswer + "%@!" +
                    faseFutebolInstance.id
        }
    }

    @Transactional
    def exportQuestions(){
        //popula a lista de questoes a partir do ID de cada uma
        ArrayList<Integer> list_questionId = new ArrayList<Integer>() ;
        ArrayList<FaseFutebol> questionList = new ArrayList<FaseFutebol>();
        list_questionId.addAll(params.list_id);
        for (int i=0; i<list_questionId.size();i++)
            questionList.add(FaseFutebol.findById(list_questionId[i]));

        //cria o arquivo json
        createJsonFile("campo.json", questionList)

        // Finds the created file path
        def folder = servletContext.getRealPath("/data/${springSecurityService.currentUser.id}/${session.taskId}")
        String id = MongoHelper.putFile("${folder}/campo.json")

        def port = request.serverPort
        if (Environment.current == Environment.DEVELOPMENT) {
            port = 8080
        }
        // Updates current task to 'completed' status
        render  "http://${request.serverName}:${port}/process/task/complete/${session.taskId}?files=${id}"
      }

    void createJsonFile(String fileName, questionList){
        def dataPath = servletContext.getRealPath("/data")
        def instancePath = new File("${dataPath}/${springSecurityService.currentUser.id}/${session.taskId}")
        instancePath.mkdirs()

        File file = new File("$instancePath/"+fileName);
        def pw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))
        pw.write("{\n")
        pw.write("\t\"desafio1\": [\"" + questionList[0].title + "\",\""+questionList[0].correctAnswer+"\"],\n")
        pw.write("\t\"desafio1\": [\"" + questionList[1].title + "\",\""+questionList[1].correctAnswer+"\"],\n")
        pw.write("}");
        pw.close();
    }
}
