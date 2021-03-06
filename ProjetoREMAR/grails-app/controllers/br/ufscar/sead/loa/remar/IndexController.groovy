package br.ufscar.sead.loa.remar

import grails.util.Environment

class IndexController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def springSecurityService

    def index() {
        if (springSecurityService.isLoggedIn()) {
                def model = [:]

                model.resourceInstanceList = Resource.findAllByStatus('approved', [max: 8, sort: "name", order: "asc"])
                model.userName = session.user.firstName
                //model.publicExportedResourcesList = ExportedResource.findAllByType('public', [max: 8, sort: "id", order: "desc"])
                //model.myExportedResourcesList = ExportedResource.findAllByTypeAndOwner('public', User.get(session.user.id), [max: 8, sort: "id", order: "desc"])
                render view: "dashboard", model: model
        } else {
            renderHTML('index.html')
        }
    }

    def apresentacao() {
        renderHTML('apresentacao.html')
    }

    def arquitetura() {
        renderHTML('arquitetura.html')
    }

    def equipe() {
        renderHTML('equipe.html')
    }

    def publicacoes() {
        renderHTML('publicacoes.html')
    }

    def contato() {
        renderHTML('contato.html')
    }

    def renderHTML(String fileName) {
        def dir = servletContext.getRealPath("/static")
        def file = new File(dir, fileName)
        def htmlContent = file.text
        render text: htmlContent, contentType:"text/html", encoding:"UTF-8"
    }

    def login() {
        if (springSecurityService.isLoggedIn()) {
            index()
        } else {
            render view: "../login/auth"
        }
    }

    def frame() {
        def model = [:]
        model.development = Environment.current == Environment.DEVELOPMENT
        model.uri = params.remove('uri')

        params.remove("controller")
        params.remove("action")
        params.remove("format")

        model.uri += "?" + params.collect { k, v -> "$k=$v" }.join('&')

        if (model.development) {
            if (model.uri.indexOf('forca') != -1) {
                model.uri = "http://localhost:8010${model.uri}"
            } else if (model.uri.indexOf('escola') != -1) {
                model.uri = "http://localhost:8020${model.uri}"
            } else if (model.uri.indexOf('mahjong') != -1) {
                model.uri = "http://localhost:8030${model.uri}"
            } else if (model.uri.indexOf('responda') != -1) {
                model.uri = "http://localhost:8040${model.uri}"
            } else if (model.uri.indexOf('ortotetris') != -1) {
                model.uri = "http://localhost:8050${model.uri}"
            } else if (model.uri.indexOf('santograu') != -1) {
                model.uri = "http://localhost:8060${model.uri}"
            } else if (model.uri.indexOf('labteca') != -1) {
                model.uri = "http://localhost:8070${model.uri}"
            } else if (model.uri.indexOf('memoria') != -1) {
                model.uri = "http://localhost:8090${model.uri}"
            }

        }

        render view: "frame", model: model
    }

    def nullPointerException(final NullPointerException exception) {
        log.debug "NullPointerException caught !"
        if (exception.getMessage().contains("firstName")) {
            log.debug "Logout: session.user is NULL !"
            redirect controller: "logout", action: "index"
        }
    }
}