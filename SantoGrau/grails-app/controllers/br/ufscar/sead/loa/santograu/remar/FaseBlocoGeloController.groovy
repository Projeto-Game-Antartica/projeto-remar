package br.ufscar.sead.loa.santograu.remar

import br.ufscar.sead.loa.remar.api.MongoHelper
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Environment
import org.springframework.web.multipart.MultipartFile

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured(["isAuthenticated()"])
class FaseBlocoGeloController {
    def springSecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "DELETE", returnInstance: "GET"]

    @Secured(['permitAll'])
    def index(Integer max) {
        if (params.t) {
            session.taskId = params.t
        }
        session.user = springSecurityService.currentUser

        def list = QuestionFaseBlocoGelo.findAllByOwnerId(session.user.id)

        if(list.size()==0){
            new QuestionFaseBlocoGelo(title: "Questão 1", answers: ["Alternativa A", "Alternativa B", "Alternativa C"], correctAnswer: 0, ownerId:  session.user.id, taskId: session.taskId).save flush: true
            new QuestionFaseBlocoGelo(title: "Questão 2", answers: ["Alternativa A", "Alternativa B", "Alternativa C"], correctAnswer: 0, ownerId:  session.user.id, taskId: session.taskId).save flush: true
            new QuestionFaseBlocoGelo(title: "Questão 3", answers: ["Alternativa A", "Alternativa B", "Alternativa C"], correctAnswer: 0, ownerId:  session.user.id, taskId: session.taskId).save flush: true
            new QuestionFaseBlocoGelo(title: "Questão 4", answers: ["Alternativa A", "Alternativa B", "Alternativa C"], correctAnswer: 0, ownerId:  session.user.id, taskId: session.taskId).save flush: true
        }

        list = QuestionFaseBlocoGelo.findAllByOwnerId(session.user.id)
        respond list, model: [faseBlocoGeloInstanceCount: QuestionFaseBlocoGelo.count()]
    }

    def show(QuestionFaseBlocoGelo faseBlocoGeloInstance) {
        respond faseBlocoGeloInstance
    }

    def create() {
        respond new QuestionFaseBlocoGelo(params)
    }

    @Transactional
    def save(QuestionFaseBlocoGelo questionFaseBlocoGeloInstance) {
        if (questionFaseBlocoGeloInstance == null) {
            notFound()
            return
        }

        questionFaseBlocoGeloInstance.answers[0]= params.answers1
        questionFaseBlocoGeloInstance.answers[1]= params.answers2
        questionFaseBlocoGeloInstance.answers[2]= params.answers3
        questionFaseBlocoGeloInstance.ownerId = session.user.id as long
        questionFaseBlocoGeloInstance.taskId = session.taskId as String
        questionFaseBlocoGeloInstance.save flush:true

        redirect(action: "index")
    }

    def edit(QuestionFaseBlocoGelo faseBlocoGeloInstance) {
        respond faseBlocoGeloInstance
    }

    @Transactional
    def update() {
        QuestionFaseBlocoGelo questionFaseBlocoGeloInstance = QuestionFaseBlocoGelo.findById(Integer.parseInt(params.faseBlocoGeloID))
        questionFaseBlocoGeloInstance.title = params.title
        questionFaseBlocoGeloInstance.answers[0]= params.answers1
        questionFaseBlocoGeloInstance.answers[1]= params.answers2
        questionFaseBlocoGeloInstance.answers[2]= params.answers3
        questionFaseBlocoGeloInstance.correctAnswer = Integer.parseInt(params.correctAnswer)
        questionFaseBlocoGeloInstance.ownerId = session.user.id as long
        questionFaseBlocoGeloInstance.taskId = session.taskId as String
        questionFaseBlocoGeloInstance.save flush:true

        redirect action: "index"
    }

    @Transactional
    def delete(QuestionFaseBlocoGelo questionFaseBlocoGeloInstance) {

        if (questionFaseBlocoGeloInstance == null) {
            notFound()
            return
        }

        questionFaseBlocoGeloInstance.delete flush:true
        redirect action: "index"
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'faseBlocoGelo.label', default: 'QuestionFaseBlocoGelo'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    @Secured(['permitAll'])
    def returnInstance(QuestionFaseBlocoGelo questionFaseBlocoGeloInstance){
        if (questionFaseBlocoGeloInstance == null) {
            //notFound()
            render "null"
        }
        else{
            render questionFaseBlocoGeloInstance.title + "%@!" +
                    questionFaseBlocoGeloInstance.answers[0] + "%@!" +
                    questionFaseBlocoGeloInstance.answers[1] + "%@!" +
                    questionFaseBlocoGeloInstance.answers[2] + "%@!" +
                    questionFaseBlocoGeloInstance.correctAnswer + "%@!" +
                    questionFaseBlocoGeloInstance.id
        }

    }

    @Transactional
    def exportQuestions(){
        //popula a lista de questoes a partir do ID de cada uma
        ArrayList<Integer> list_questionId = new ArrayList<Integer>() ;
        ArrayList<QuestionFaseBlocoGelo> questionList = new ArrayList<QuestionFaseBlocoGelo>();
        list_questionId.addAll(params.list_id);
        for (int i=0; i<list_questionId.size();i++)
            questionList.add(QuestionFaseBlocoGelo.findById(list_questionId[i]));

        //cria o arquivo json
        createJsonFile("questoesbn.json", questionList)

        // Finds the created file path
        def folder = servletContext.getRealPath("/data/${springSecurityService.currentUser.id}/${session.taskId}")
        String id = MongoHelper.putFile("${folder}/questoesbn.json")


        def port = request.serverPort
        if (Environment.current == Environment.DEVELOPMENT) {
            port = 8080
        }

        // Updates current task to 'completed' status
        render  "http://${request.serverName}:${port}/process/task/complete/${session.taskId}?files=${id}"


    }

    void createJsonFile(String fileName, ArrayList<QuestionFaseBlocoGelo> questionList){
        def dataPath = servletContext.getRealPath("/data")
        def instancePath = new File("${dataPath}/${springSecurityService.currentUser.id}/${session.taskId}")
        instancePath.mkdirs()

        File file = new File("$instancePath/"+fileName);
        PrintWriter pw = new PrintWriter(file);
        pw.write("{\n")
        pw.write("\t\"quantidadeQuestoes\": [\"" + questionList.size() + "\"],\n")
        for(def i=0; i<questionList.size();i++){
            pw.write("\t\"" + (i+1) + "\": [\"" + questionList[i].title + "\", ")
            pw.write("\""+ questionList[i].answers[0] +"\", " + "\""+ questionList[i].answers[1] +"\", ")
            pw.write("\""+ questionList[i].answers[2] +"\", ")
            switch(questionList[i].correctAnswer){
                case 0:
                    pw.write("\"A\"]")
                    break;
                case 1:
                    pw.write("\"B\"]")
                    break;
                case 2:
                    pw.write("\"C\"]")
                    break;
                default:
                    println("Erro! Alternativa correta inválida")
            }
            if(i<questionList.size()-1)
                pw.write(",")
            pw.write("\n")
        }
        pw.write("}");
        pw.close();
    }

    @Transactional
    def generateQuestions(){
        MultipartFile csv = params.csv

        csv.inputStream.toCsvReader([ 'separatorChar': ';']).eachLine { row ->
            QuestionFaseBlocoGelo questionInstance = new QuestionFaseBlocoGelo()
            questionInstance.title = row[1] ?: "NA";
            questionInstance.answers[0] = row[2] ?: "NA";
            questionInstance.answers[1] = row[3] ?: "NA";
            questionInstance.answers[2] = row[4] ?: "NA";
            String correct = row[7] ?: "NA";
            questionInstance.correctAnswer =  (correct.toInteger() - 1)
            questionInstance.taskId = session.taskId as String
            questionInstance.ownerId = session.user.id as long
            questionInstance.save flush: true
            println(questionInstance.taskId)
            println(questionInstance)
            println(questionInstance.errors)
        }
        redirect(action: index())
    }

    def exportCSV(){
        /* Função que exporta as questões selecionadas para um arquivo .csv genérico.
           O arquivo .csv gerado será compatível com os modelos Escola Mágica, Forca e Responda Se Puder.
           O arquivo gerado possui os seguintes campos na ordem correspondente:
           Nível, Pergunta, Alternativa1, Alternativa2, Alternativa3, Alternativa4, Alternativa5, Alternativa Correta, Dica, Tema.
           O campo Dica é correspondente ao modelo Responda Se Puder e o campo Tema ao modelo Forca.
           O separador do arquivo .csv gerado é o ";" (ponto e vírgula)
        */

        ArrayList<Integer> list_questionId = new ArrayList<Integer>() ;
        ArrayList<QuestionFaseCampoMinado> questionList = new ArrayList<QuestionFaseCampoMinado>();
        list_questionId.addAll(params.list_id);
        for (int i=0; i<list_questionId.size();i++){
            questionList.add(QuestionFaseCampoMinado.findById(list_questionId[i]));

        }

        def dataPath = servletContext.getRealPath("/samples")
        def instancePath = new File("${dataPath}/export")
        instancePath.mkdirs()
        log.debug instancePath

        def fw = new FileWriter("$instancePath/exportQuestions.csv")
        for(int i=0; i<questionList.size();i++){
            fw.write("nivel;" + questionList.getAt(i).title + ";" + questionList.getAt(i).answers[0] + ";" + questionList.getAt(i).answers[1] + ";" +
                    questionList.getAt(i).answers[2] + (questionList.getAt(i).correctAnswer +1) + ";dica;tema" +";\n" )
        }
        fw.close()

        def port = request.serverPort
        if (Environment.current == Environment.DEVELOPMENT) {
            port = 8080
        }

        render "/santograu/samples/export/exportQuestions.csv"
    }
}
