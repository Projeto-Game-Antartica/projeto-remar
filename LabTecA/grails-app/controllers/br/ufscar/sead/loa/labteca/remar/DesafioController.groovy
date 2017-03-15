package br.ufscar.sead.loa.labteca.remar

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import br.ufscar.sead.loa.remar.api.MongoHelper

@Transactional(readOnly = true)
@Secured('ROLE_ADMIN')
class DesafioController {
    def springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        if (params.t)
            session.taskID = params.t

        if (params.p)
            session.processID = params.p

        session.user = springSecurityService.currentUser

        def compostos = Composto.findAll()
        def tipos = [Composto.SAL, Composto.BASE, Composto.ACIDO, Composto.ORGANICO, Composto.ETC]

        respond compostos, model: [tiposCompostoList: tipos]
    }

    def show(Desafio desafioInstance) {
        respond desafioInstance
    }

    def create() {
        respond new Desafio(params)
    }

    @Transactional
    def save(Desafio desafioInstance) {
        if (desafioInstance == null) {
            notFound()
            return
        }

        if (desafioInstance.hasErrors()) {
            respond desafioInstance.errors, view:'create'
            return
        }

        desafioInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'desafio.label', default: 'Desafio'), desafioInstance.id])
                redirect desafioInstance
            }
            '*' { respond desafioInstance, [status: CREATED] }
        }
    }

    def edit(Desafio desafioInstance) {
        respond desafioInstance
    }

    @Transactional
    def update(Desafio desafioInstance) {
        if (desafioInstance == null) {
            notFound()
            return
        }

        if (desafioInstance.hasErrors()) {
            respond desafioInstance.errors, view:'edit'
            return
        }

        desafioInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Desafio.label', default: 'Desafio'), desafioInstance.id])
                redirect desafioInstance
            }
            '*'{ respond desafioInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Desafio desafioInstance) {

        if (desafioInstance == null) {
            notFound()
            return
        }

        desafioInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Desafio.label', default: 'Desafio'), desafioInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'desafio.label', default: 'Desafio'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    @Secured(['permitAll'])
    def export() {
        // cria a instância do desafio com os valores digitados pelo usuario
        Desafio desafio = new Desafio()
        desafio.composto = params.compostoInstance
        desafio.volInicial = params.volInicial
        desafio.molInicial = params.molInicial
        desafio.volFinal = params.volFinal
        desafio.molFinal = params.molFinal

        // cria o arquivo json do desafio
        createJournalItemsFile("journalItems0.json", desafio)
        createCustomPhaseFile("customPhase.json", desafio)

        // encontra o endereço do arquivo criado
        def folder = servletContext.getRealPath("/data/${springSecurityService.currentUser.id}/${session.taskId}")

        log.debug folder
        def ids = []
        ids << MongoHelper.putFile(folder + "journalItems0.json")
        ids << MongoHelper.putFile(folder + "customPhase.json")

        def port = request.serverPort
        if (Environment.current == Environment.DEVELOPMENT) {
            port = 8080
        }

        // atualiza a tarefa corrente para o status de "completo"
        render  "http://${request.serverName}:${port}/process/task/complete/${session.taskId}" +
                "?files=${ids[0]}&files=${ids[1]}}"
    }

    void createJournalItemsFile(String filename, Desafio desafio) {
        def dataPath = servletContext.getRealPath("/data")
        def instancePath = new File("${dataPath}/${springSecurityService.currentUser.id}/${session.taskId}")

        instancePath.mkdirs()

        File file = new File("$instancePath/" + fileName)
        PrintWriter pw = new PrintWriter(file)

        // Impressão do header
        pw.write("{\n")
        pw.write("\t\"name\" : \"Análise da Solução Fornecida\",\n")
        pw.write("\t\"objects\": [\n")
        // Impressão da Etapa 1 (descobrir classe do composto)
        pw.write("\t{\n")
        pw.write("\t\t\"index\" : \"0\",\n")
        pw.write("\t\t\"name\" : \"Utilize os equipamentos e seus conhecimentos de química analítica para descobrir a classe do composto presente na solução.\",\n")
        pw.write("\t\t\"isDone\" : \"False\",\n")
        pw.write("\t\t\"numberOfPrerequisites\" : \"0\"\n")
        pw.write("\t},\n")
        // Impressão da Etapa 2 (descobrir o composto)
        pw.write("\t{\n")
        pw.write("\t\t\"index\" : \"1\",\n")
        pw.write("\t\t\"name\" : \"Com o auxílio dos equipamentos, descobra qual é o composto presente na solução.\",\n")
        pw.write("\t\t\"isDone\" : \"False\",\n")
        pw.write("\t\t\"numberOfPrerequisites\" : \"1\",\n")
        pw.write("\t\t\"indexPrerequisiteOf\" : \"[0]\"\n")
        pw.write("\t},\n")
        // Impressão da Etapa 3 (alterar a solução do composto)
        pw.write("\t{\n")
        pw.write("\t\t\"index\" : \"2\",\n")
        pw.write("\t\t\"name\" : \"Altere o volume da solução para " + desafio.volFinal + "ml e a molaridade para " + desafio.molFinal + " mol/L.\",\n")
        pw.write("\t\t\"isDone\" : \"False\",\n")
        pw.write("\t\t\"numberOfPrerequisites\" : \"1\",\n")
        pw.write("\t\t\"indexPrerequisiteOf\" : \"[1]\"\n")
        pw.write("\t}]\n")
        pw.write("}")
    }

    void createCustomPhaseFile(String filename, Desafio desafio) {
        def dataPath = servletContext.getRealPath("/data")
        def instancePath = new File("${dataPath}/${springSecurityService.currentUser.id}/${session.taskId}")
        instancePath.mkdirs()

        File file = new File("$instancePath/" + fileName)
        PrintWriter pw = new PrintWriter(file)

        // Impressão do header
        pw.write("{\n")
        pw.write("\t\"glasswareStart\" : \"true\",\n")
        pw.write("\t\"compoundFormula\" : \"" + desafio.composto.formula + "\",\n")
        pw.write("\t\"volume\" : \"" + desafio.volInicial + "\",\n")
        pw.write("\t\"molarity\" : \"" + desafio.molInicial + "\",\n")
        pw.write("\t\"objects\" : [\n")
        // Impressão do Desafio 1
        pw.write("\t{\n")
        pw.write("\t\t\"typeOfStep\" : \"1\",\n")
        pw.write("\t\t\"option1\" : \"Sal\",\n")
        pw.write("\t\t\"option2\" : \"Base\",\n")
        pw.write("\t\t\"option3\" : \"Ácido\",\n")
        pw.write("\t\t\"option4\" : \"Orgânico\",\n")
        pw.write("\t\t\"option5\" : \"Etc\",\n")

        def correctAnswer = [
                "Sal" : 1,
                "Base" : 2,
                "Ácido" : 3,
                "Orgânico" : 4,
                "Etc" : 5
        ]

        pw.write("\t\t\"correctAnswer\" : \"" + correctAnswer.getOrDefault(desafio.composto.tipo, 5) + "\",\n")
        pw.write("\t},\n")
        // Impressão do Desafio 2
        pw.write("\t{\n")
        pw.write("\t\t\"typeOfStep\" : \"2\",\n")
        pw.write("\t\t\"compoundFormula\" : \"" + desafio.composto.formula + "\",\n")
        pw.write("\t},\n")
        // Impressão do Desafio 3
        pw.write("\t{\n")
        pw.write("\t\t\"typeOfStep\" : \"3\",\n")
        pw.write("\t\t\"compoundFormula\" : \"" + desafio.composto.formula + "\",\n")
        pw.write("\t\t\"molarity\" : \"" + desafio.molInicial + "\",\n")
        pw.write("\t\t\"maxError\" : \"0.2\",\n")
        pw.write("\t},\n")
        // Impressão do Desafio 4
        pw.write("\t{\n")
        pw.write("\t\t\"typeOfStep\" : \"4\",\n")
        pw.write("\t\t\"compoundFormula\" : \"" + desafio.composto.formula + "\",\n")
        pw.write("\t\t\"molarity\" : \"" + desafio.molFinal + "\",\n")
        pw.write("\t\t\"minVolume\" : \"" + desafio.volFinal + "\",\n")
        pw.write("\t\t\"maxError\" : \"0.2\",\n")
        pw.write("\t}]\n")
        pw.write("}")
    }
}