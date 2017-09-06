package br.ufscar.sead.loa.memoria

import br.ufscar.sead.loa.remar.api.MongoHelper
import grails.util.Environment
import static org.springframework.http.HttpStatus.*
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional


@Secured(['isAuthenticated()'])
class TileController {

    def springSecurityService

    def index() {
        if (params.t) {
            session.taskId = params.t
        }
        session.user = springSecurityService.currentUser

        render  view: "index"
    }

    def show() {
        render  template: "tile",
                model: [ tileInstance: Tile.findById(params.id) ]
    }

    def create() {
        respond new Tile(params)
    }

    @Transactional
    def save(Tile tileInstance) {
        if (tileInstance == null) {
            notFound()
            return
        }

        if (tileInstance.hasErrors()) {
            respond tileInstance.errors, view:'create'
            return
        }

        def userId = session.user.id
        tileInstance.ownerId = userId
        tileInstance.taskId = session.taskId

        tileInstance.save flush: true

        def id = tileInstance.getId()
        def userPath = servletContext.getRealPath("/data/" + userId.toString() + "/tiles")
        def userFolder = new File(userPath)
        def script_convert_png = servletContext.getRealPath("/scripts/convert.sh")
        userFolder.mkdirs()

        def f1Uploaded = request.getFile("tile-a")
        def f2Uploaded = request.getFile("tile-b")
        if (!f1Uploaded.isEmpty() && !f2Uploaded.isEmpty()) {

            def f1 = new File("$userPath/tile$id-a.png")
            def f2 = new File("$userPath/tile$id-b.png")

            f1Uploaded.transferTo(f1)
            f2Uploaded.transferTo(f2)

            // the convert script will convert the files to png even if they weren't uploaded as such
            // this was needed because the file wouldn't open as png if uploaded as other format
            executarShell([
                    script_convert_png,
                    f1.absolutePath,
                    f1.absolutePath
            ])

            executarShell([
                    script_convert_png,
                    f2.absolutePath,
                    f2.absolutePath
            ])
        }

        redirect(controller: "Tile", action:"index")
    }

    def edit() {
        def tileInstance = Tile.findById(params.id)

        render  view: "edit",
                model: [
                    tileInstance: tileInstance,
                    edit: true
                ]
    }

    @Transactional
    def update() {
        def tileInstance = Tile.get(params.id)
        tileInstance.content = params.content
        tileInstance.description = params.description
        tileInstance.difficulty = params.difficulty.toInteger()

        tileInstance.save flush:true

        if (tileInstance.hasErrors()) {
            println tileInstance.errors
        }

        def id = tileInstance.getId()
        def userPath = servletContext.getRealPath("/data/" + tileInstance.ownerId.toString() + "/tiles")
        def script_convert_png = servletContext.getRealPath("/scripts/convert.sh")

        def f1Uploaded = request.getFile("tile-a")
        def f2Uploaded = request.getFile("tile-b")

        // Change tile first image if it was uploaded
        if (!f1Uploaded.isEmpty()) {
            def f1 = new File("$userPath/tile$id-a.png")
            f1Uploaded.transferTo(f1)

            // convert to png
            executarShell([
                    script_convert_png,
                    f1.absolutePath,
                    f1.absolutePath
            ])
        }

        // Change tile second image if it was uploaded
        if (!f2Uploaded.isEmpty()) {
            def f2 = new File("$userPath/tile$id-b.png")
            f2Uploaded.transferTo(f2)

            // convert to png
            executarShell([
                    script_convert_png,
                    f2.absolutePath,
                    f2.absolutePath
            ])
        }

        redirect(controller: "Tile", action:"index")
    }

    @Transactional
    def delete() {
        def tileInstance = Tile.findById(params.id)

        if (tileInstance == null) {
            notFound()
            return
        }

        // delete tile images before removing them from database
        def tiles = getTilesImages(tileInstance)

        def f1 = new File(tiles.a)
        def f2 = new File(tiles.b)
        f1.delete()
        f2.delete()

        tileInstance.delete flush:true

        redirect(controller: "Tile", action:"index")
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'tile.label', default: 'Tile'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def listByDifficulty() {
        def owner = session.user.id
        def taskId = session.taskId
        def difficulty = params.difficulty.toInteger()
        def tileList = Tile.findAllByDifficultyAndOwnerIdAndTaskId(difficulty, owner, taskId)

        // se a lista de peças não for vazia, renderiza o select com as peças
        if (tileList == null || tileList.empty) {
            // status 412: precondition_failed
            // uma ou mais condições testadas pelo servidos foram avaliadas como falsas ou falharam
            render (status: 412, template: "empty")
        } else {
            render (status: 200, template: "select", model: [tileList: tileList])
        }
    }

    def validate() {
        def owner = springSecurityService.currentUser.getId()
        def message = new StringBuilder()
        def difficulties = ['Fácil', 'Médio', 'Difícil']
        def ok = true

        for (def difficulty = 1; difficulty <= 3; difficulty++) {
            //def min = difficulty * 2 + 2
            def min = 1
            def count = Tile.countByDifficultyAndOwnerIdAndTaskId(difficulty, owner, session.taskId)

            if (count < min) {
                message << 'A dificuldade <b>' << difficulties[difficulty - 1] << '</b> deve ter no mínimo <b>' << min << ' peças</b> e você fez o upload de <b>' << count << ' peças</b>. <br/>'
                ok = false
            }
        }

        if (!ok) {
            render message.toString()
            return
        } else {
            // gera os arquivos: output.CSS e cartas.png
            generateTileSet(params.orientation)

            // encontra o endereço do arquivo criado
            def folder = servletContext.getRealPath("/data/${springSecurityService.currentUser.id}")

            log.debug folder
            def ids = []
            ids << MongoHelper.putFile("${folder}/output.css")
            ids << MongoHelper.putFile("${folder}/tiles/cartas.png")

            //if (! new File(folder).deleteDir()){
            //    println("Erro em tentar excluir a pasta do usuario")
            //}

            def port = request.serverPort
            if (Environment.current == Environment.DEVELOPMENT) {
                port = 8080
            }

            // atualiza a tarefa corrente para o status de "completo"
            render  "http://${request.serverName}:${port}/process/task/complete/${session.taskId}" +
                    "?files=${ids[0]}&files=${ids[1]}"
        }
    }

    def generateTileSet(orientation) {
        // this method will execute 3 different shell scripts in order to create the cartas.png and the CSS that will be
        // automatically generated according to what the user has uploaded

        ////////////////////////////////////////////////////////////////////////////////////////
        // Params script concatenate
        // $1 = -v or -h (vertical or horizontal)
        // $2 = destino (facil, medio, dificil)
        // $3 = path for "tiles" directory

        // this script will create 3 different "decks" in a different image each
        def dataPath = servletContext.getRealPath("/data")
        def instancePath = "${dataPath}/${springSecurityService.currentUser.id}"
        def tilesPath = "${instancePath}/tiles"
        def owner = session.user.id

        def easyTilesIdList   = Tile.findAllByDifficultyAndOwnerIdAndTaskId(1, owner, session.taskId)*.id
        def mediumTilesIdList = Tile.findAllByDifficultyAndOwnerIdAndTaskId(2, owner, session.taskId)*.id
        def hardTilesIdList   = Tile.findAllByDifficultyAndOwnerIdAndTaskId(3, owner, session.taskId)*.id

        execConcatenate(
                "-${orientation}",
                "facil",
                easyTilesIdList,
                tilesPath
        )

        execConcatenate(
                "-${orientation}",
                "medio",
                mediumTilesIdList,
                tilesPath
        )

        execConcatenate(
                "-${orientation}",
                "dificil",
                hardTilesIdList,
                tilesPath
        )

        ////////////////////////////////////////////////////////////////////////////////////////
        // Parametros script Append
        // #$1 = tiles folder
        // #$2 = orientation

        // this script appends the "flipped" card to the final image
        def script_append = servletContext.getRealPath("/scripts/append.sh")

        def l2 = [
            script_append,
            tilesPath,
            orientation
        ]

        println("l2 --> " + l2)
        executarShell(l2)
        ////////////////////////////////////////////////////////////////////////////////////////
        // Parametros script sedSASS
        //#1 - full path for template.scss
        //#2 - full path for output.css
        //#3 - parametro que substituirá char_orientacao no script sass
        //#4 - parametro que substituirá facilPares no script sass
        //#5 - parametro que substituirá medioPares no script sass
        //#6 - parametro que substituirá dificilPares no script sass

        // this script will call the sass script, which will create the css file accordingly to our parameters
        def script_sedSASS = servletContext.getRealPath("/scripts/sed_sass.sh")

        def l3 = [
            script_sedSASS,
            servletContext.getRealPath("/scripts/template.scss"),
            "${instancePath}/output.css",
            orientation,
            easyTilesIdList.size(),
            mediumTilesIdList.size(),
            hardTilesIdList.size()
        ]

        println("l3 --> " + l3)
        executarShell(l3)
    }

    def execConcatenate(orient, difficulty, idList, folder) {

        def script_concatenate_tiles = servletContext.getRealPath("/scripts/concatenate.sh")
        def l = [
            script_concatenate_tiles,
            orient, // $1
            difficulty, // $2
            folder // $3
        ]

        // adding parameters to the script (name of the img files to be appended)
        // this 'for' needs to be done twice because of the order of the param files in the concatenate script
        for (id in idList)
            l.add("tile${id}-a.png")

        for (id in idList)
            l.add("tile${id}-b.png")

        executarShell(l)

    }

    // the list has to contain the path to the sh file as its first element
    // and then the next elements will be the respective params for the script
    def executarShell(execList){
        def proc

        proc = execList.execute()
        proc.waitFor()
        if (proc.exitValue()) {
            println "script ${execList.get(0)} gave the following error: "
            println "[ERROR] ${proc.getErrorStream()}"
        }

    }

    // return list with filenames for images related to a tile pair
    def getTilesImages(tileInstance) {
        def userPath = servletContext.getRealPath("/data/" + tileInstance.ownerId.toString() + "/tiles")
        def id = tileInstance.getId()
        def images = [
            "a": "$userPath/tile$id-a.png",
            "b": "$userPath/tile$id-b.png"
        ]

        return images
    }

}