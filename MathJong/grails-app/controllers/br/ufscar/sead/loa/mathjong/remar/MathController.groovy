package br.ufscar.sead.loa.mathjong.remar

import br.ufscar.sead.loa.remar.User
import br.ufscar.sead.loa.remar.api.MongoHelper
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Environment
import groovy.json.JsonBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

@Secured(["IS_AUTHENTICATED_FULLY"])
class MathController {

    def springSecurityService

    @Secured(["permitAll"])
    def index(Integer max) {
        session.user = springSecurityService.currentUser

        // params.max = Math.min(max ?: 10, 100)
        params.max = 100 // TODO

        if (params.t && params.h) {
            session.taskId = params.t

            def u = User.findByUsername(new String(params.h.decodeBase64()))
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(u, null, u.authoritiesHashSet()))

            redirect controller: "math" // redirect to hide params.h
            return
        }

        render view: "index"
    }

    def save() {
        def nCols = params.int "nCols"
        def nLines = params.int "nLines"
        def _time = nCols * nLines * 15
        def latex = params.list "latex[]"

        def _data = []
        def k = 0

        def builder = new JsonBuilder()

        nLines.times {
            def line = []
            nCols.times {
                line.add "\$\$" + latex[k++] + "\$\$"
            }
            _data.add line
        }

        builder {
            linha nLines
            coluna nCols
            time _time
            data _data
        }

        def folder = new File(servletContext.getRealPath("/data/${session.user.id}/${session.taskId}"))
        folder.mkdirs()

        def fileName = "fases.json"
        def file = new File("${folder}/${fileName}")

        if (!file.exists()) {
            file.createNewFile()
            file.append('[')
        }

        file.append(builder.toString() + ",")

        render 200
        response.status = 200
    }

    def finish() {
        def file = new File(servletContext.getRealPath("/data/${session.user.id}/${session.taskId}/fases.json"))
        def json = file.text
        json = json.substring(0, json.length() - 1)
        json += "]"
        file.write(json)

        String id = MongoHelper.putFile(file.absolutePath)

        def port = request.serverPort
        if (Environment.current == Environment.DEVELOPMENT) {
            port = 8080
        }

        redirect uri: "http://${request.serverName}:${port}/process/task/complete/${session.taskId}", params: [files: id]
    }
}