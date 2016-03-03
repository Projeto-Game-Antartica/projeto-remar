package br.ufscar.sead.loa.remar

import grails.converters.JSON
import grails.util.Environment
import groovyx.net.http.HTTPBuilder
import org.apache.commons.lang.RandomStringUtils
import org.codehaus.groovy.grails.io.support.GrailsIOUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.security.MessageDigest

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

class ResourceController {

    static allowedMethods = [save: "POST", update: "POST", delete: "DELETE"]
    def springSecurityService

    def index(Integer max) {

        if (request.isUserInRole("ROLE_ADMIN")) {
            render view: 'index', model:[resourceInstanceList: Resource.list(), resourceInstanceCount: Resource.count()]
        } else {
            render view: 'index', model:[resourceInstanceList: Resource.findAllByOwner(springSecurityService.currentUser as User), resourceInstanceCount: Resource.count()]
        }

    }


    def create() {
        render view: "create", model: [id: params.id]
    }

    @Transactional
    def update(Resource instance){
        def path = new File(servletContext.getRealPath("/data/resources/assets/${instance.uri}"))
        path.mkdirs()

//        MultipartFile img1 = request.getFile('img1')
//        MultipartFile img2 = request.getFile('img2')
//        MultipartFile img3 = request.getFile('img3')

        log.debug(params)

        if(params.img1 != null && params.img1 != ""){
            log.debug("entrou img1" + params.img1)
            def img1 = new File(servletContext.getRealPath("${params.img1}"))
            img1.renameTo(new File(path,"description-1"))
        }

        if(params.img2 != null && params.img2 != ""){
            def img2 = new File(servletContext.getRealPath("${params.img2}"))
            img2.renameTo(new File(path,"description-2"))
        }

        if(params.img3 != null && params.img3 != ""){
            def img3 = new File(servletContext.getRealPath("${params.img3}"))
            img3.renameTo(new File(path,"description-3"))
        }

        instance.description = params.description
        instance.name = params.name
        instance.comment = "Em avaliação"
        //TODO colocar a categoria

        instance.save flush: true

        render true;
//       redirect action: "index", params: [id: resourceInstance.id]
    }

    @Transactional
    def save(Resource resourceInstance) { // saves and verifies WAR file
        String username = session.user.username
        MultipartFile submitedWar = params.war
        String fileName = MessageDigest.getInstance("MD5").digest(submitedWar.bytes).encodeHex().toString()
        File savedWar = new File(servletContext.getRealPath("/wars/${username}"), fileName + ".war")
        String expandedWarPath = savedWar.parent + "/" + fileName
        File tmp
        AntBuilder ant = new AntBuilder()

        resourceInstance.submittedAt = new Date()
        resourceInstance.owner       = springSecurityService.currentUser as User
        resourceInstance.status      = "pending"
        resourceInstance.valid       = true

        // Move .war to /wars and unzip it
        savedWar.mkdirs()
        submitedWar.transferTo(savedWar)
        ant.mkdir(dir: expandedWarPath)
        ant.unzip(src: savedWar.path, dest: expandedWarPath, overwrite: true)
        println "done"
        println expandedWarPath
        tmp = new File(expandedWarPath + "/WEB-INF")
        println tmp
        if (!tmp.exists()) { // file is not a WAR
            resourceInstance.name = submitedWar.originalFilename
            this.rejectWar(resourceInstance, "File is not a WAR")
            redirect action: "index"
            return
        }

        tmp = new File(expandedWarPath + "/remar/manifest.json")
        if (!tmp.exists()) { // manifest.json not found
            resourceInstance.name = submitedWar.originalFilename
            this.rejectWar(resourceInstance, 'manifest.json not found')
            redirect action: "index"
            return
        }

        def manifest = JSON.parse(tmp.getText('UTF-8'))

        // manifest is valid?
        if (manifest.name  == null || manifest.android  == null || manifest.linux  == null || manifest.moodle  == null
            || manifest.bpmn  == null || manifest.uri == null || manifest.width == null || manifest.height == null) {
            resourceInstance.name = submitedWar.originalFilename
            this.rejectWar(resourceInstance, 'Invalid manifest.json')
            redirect action: "index"
            return
        }

        resourceInstance.name    = manifest.name
        resourceInstance.uri     = manifest.uri
        resourceInstance.android = manifest.android
        resourceInstance.linux   = manifest.linux
        resourceInstance.moodle  = manifest.moodle
        resourceInstance.files   = manifest.files
        resourceInstance.width   = manifest.width
        resourceInstance.height  = manifest.height
        resourceInstance.bpmn = manifest.bpmn

        //read the file that describes the game DB and creates a collection with the corresponding name
        def bd = new File(expandedWarPath + "/remar/bd.json")

        if (!bd.exists()) {
            this.rejectWar(resourceInstance, 'bd.json not found')
            redirect action: "index"
            return
        }

        new AntBuilder().copy(file: expandedWarPath + "/remar/bd.json",
                tofile: servletContext.getRealPath("/data/resources/sources/${resourceInstance.uri}/bd.json"))

        def json = JSON.parse(bd.text)
        def collectionName = json['collection_name'] as String
        MongoHelper.instance.createCollection(collectionName)

        log.debug "Collection '${collectionName}' successfully created."

        tmp = new File("${expandedWarPath}/remar/images/${resourceInstance.uri}-banner.png")
        if (!tmp.exists()) { // {uri}-banner.png not found
            this.rejectWar(resourceInstance, 'banner not found')
            redirect action: "index"
            return
        } else {
            ant.copy(file: tmp, todir: servletContext.getRealPath("/images"))
        }

        tmp = new File("${expandedWarPath}/remar/process.json")
        if (!tmp.exists()) { // bmpn not found
            this.rejectWar(resourceInstance, 'bpmn not found')
            redirect action: "index"
            return
        } else {
            ant.mkdir(dir: servletContext.getRealPath("/propeller"))
            ant.copy(file: tmp, todir: servletContext.getRealPath("/propeller/${resourceInstance.uri}"))
        }

        tmp = new File("${expandedWarPath}/remar/source")
        if (!tmp.exists()) { // source folder exists?
            this.rejectWar(resourceInstance, "game's source folder not found")
            redirect action: "index"
            return
        } else {
            ant.copy(todir: servletContext.getRealPath("/data/resources/sources/${resourceInstance.uri}")) {
                fileset(dir: tmp)
            }
        }

        resourceInstance.web = true //default
        resourceInstance.comment = "Esperando Formulário"

        // rename war to a human readable name – instead of a MD5 name
        savedWar.renameTo(servletContext.getRealPath("/wars/${username}") + "/${resourceInstance.uri}.war")

        resourceInstance.save flush:true

        if(resourceInstance.hasErrors()) {
            log.error "War submited by " + session.user.username + " rejected by Grails. Reason:"
            log.error resourceInstance.errors

            respond resourceInstance.errors, view:"create"
        } else {
            flash.message = message(code: 'default.created.message', args: [message(code: 'deploy.label', default: 'Deploy'), resourceInstance.id])
            render resourceInstance as JSON
        }

    }

    def test() {
        "chmod a+x ${servletContext.getRealPath("/scripts/test.sh")}".execute().waitFor()
        render "${servletContext.getRealPath("/scripts/test.sh")}".execute().text
    }

    def newDeveloper(){


    }

    def review() {
        def resourceInstance = Resource.findById(params.id)
        String status  = params.status
        String comment = params.comment

        if(!status) {
            resourceInstance.comment = comment
            if(resourceInstance.status == "rejected") {
                Util.sendEmail(resourceInstance.owner.email,
                        "REMAR – O seu WAR \"${resourceInstance.name}\" foi rejeitado!",
                        "<h3>O seu WAR \"${resourceInstance.name}\" foi rejeitado pois ${comment}</h3> <br> "
                )
                render 'success'
            }
        }

        if (status == "approve" && resourceInstance.status != "approved") {

            "${servletContext.getRealPath("/scripts/db.sh")} ${resourceInstance.uri}".execute().waitFor()

            if (Environment.current == Environment.DEVELOPMENT) {
                resourceInstance.status  = "approved"
                resourceInstance.active = true
                resourceInstance.version = 0
                resourceInstance.save flush: true

                redirect controller: "process", action: "deploy", id: resourceInstance.uri
                return
            }

            def http = new HTTPBuilder("http://root:${grailsApplication.config.users.password}@localhost:8080")
            def resp = http.get(path: '/manager/text/deploy',
                    query: [path: "/${resourceInstance.uri}",
                            war: servletContext.getRealPath("/wars/${resourceInstance.owner.username}/${resourceInstance.uri}.war") ])
            resp = GrailsIOUtils.toString(resp)
            if(resp.indexOf('OK') != -1) {
                resourceInstance.status  = "approved"
                resourceInstance.active = true
                resourceInstance.version = 0
                resourceInstance.save flush: true

                //noinspection GroovyAssignabilityCheck
                Util.sendEmail(resourceInstance.owner.email,
                        "REMAR – O seu WAR \"${resourceInstance.name}\" foi aprovado!",
                        '<h3>O seu WAR \"${resourceInstance.name}\" foi aprovado! :)</h3> <br>'
                )

                redirect controller: "process", action: "deploy", id: resourceInstance.uri
            } else {
                response.status = 500
                render resp
            }
            // probably we don't need this anymore because when the WAR is deployed the bpmn is deployed too
            // redirect controller: "process", action: "deploy", id: resourceInstance.bpmn
        } else if (status == "reject" && resourceInstance.status != "rejected") {
            resourceInstance.status  = "rejected"

            render "success"
        }

        resourceInstance.save flush: true

    }

    @Transactional
    def delete(Resource resourceInstance) {

        if (resourceInstance == null) {
            log.debug "Trying to delete a resource, but that was not found."
            response.status = 404
            render "Not found"
            return
        }

        if (resourceInstance.owner == springSecurityService.currentUser || springSecurityService.currentUser == User.findByUsername('admin')) {
            resourceInstance.delete flush: true
            log.debug "Resource Deleted"
        }
        else {
            log.debug "Someone is trying to delete a resource that belongs to other user"
        }

        render "success"
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'deploy.label', default: 'Deploy'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def show(Resource instance){
        render view: "show", model: [resourceInstance : instance]
    }

    def customizableGames(){
        def model = [:]

        model.gameInstanceList = Resource.findAllByStatus('approved') // change to #findAllByActive?

        render view: "customizableGames", model: model
    }


    def edit(Resource resourceInstance) {

        def resourceJson = resourceInstance as JSON

        render view: 'edit', model:[resourceInstance: resourceInstance]
    }

    def getResourceInstance(long id){

        def r = Resource.findById(id) as JSON

        render r;
    }

    def croppicture() {
        def root = servletContext.getRealPath("/")
        def f = new File("${root}data/tmp")
        f.mkdirs()
        def destination = new File(f, RandomStringUtils.random(50, true, true))
        def photo = params.photo as CommonsMultipartFile
        photo.transferTo(destination)

        def x = Math.round(params.float('x'))
        def y = Math.round(params.float('y'))
        def w = Math.round(params.float('w'))
        def h = Math.round(params.float('h'))
        BufferedImage img = ImageIO.read(destination)
        ImageIO.write(img.getSubimage(x, y, w, h),
                photo.contentType.contains('png') ? 'png' : 'jpg', destination)
        println destination.name
        render destination.name
    }

    def rejectWar(Resource instance, String reason) {
        instance.valid = false
        instance.uri = ""
        instance.status  = "rejected"
        instance.comment = reason
        instance.save flush: true
        log.debug "War submited by " + session.user.username + " rejected. Reason: " + reason
    }
 }
