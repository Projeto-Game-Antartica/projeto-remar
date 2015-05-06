package br.ufscar.sead.loa.remar

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_EDITOR'])
class EditorController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def springSecurityService

    def index() {
        render view: 'index'
    }
}