package projetoremar

import org.h2.engine.User
import org.springframework.security.access.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
@Secured(['ROLE_PROF'])
@Transactional(readOnly = true)
class DesignController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Design.list(params), model: [designInstanceCount: Design.count()]
    }

    def show(Design designInstance) {
        respond designInstance
    }

    def create() {
        respond new Design(params)
    }

    def ShowImage(){
       def images = Design.get(params.id)
       byte[] image = images.icone

        response.contentType = "image/png"
        response.outputStream << image
        response.outputStream.flush()
        return

    }

    @Transactional
    def ImagesManager() {
     //   def designInstance = new Design(params)

        def iconUploaded = request.getFile('icone')
        def openingUploaded = request.getFile('opening')
        def backgroundUploaded = request.getFile('background')

        iconUploaded.transferTo(new File('/home/loa/Denis/REMAR/ProjetoREMAR/grails-app/assets/images/icon.png'))
        openingUploaded.transferTo(new File("/home/loa/Denis/REMAR/ProjetoREMAR/grails-app/assets/images/open.png"))
        backgroundUploaded.transferTo(new File("//home/loa/Denis/REMAR/ProjetoREMAR/grails-app/assets/images/background.png"))

        println "IMAGES MANAGER"

        redirect(controller: "design", action:"index")

    }


    @Transactional
    def save(Design designInstance) {
        if (designInstance == null) {
            notFound()
            return
        }

        if (designInstance.hasErrors()) {
            respond designInstance.errors, view: 'create'
            return
        }

        /*
        def imageUploaded = request.getFile('icone')

        if (!imageUploaded.empty) {
            println "Nome: ${imageUploaded.name}"
            println "OriginalFileName: ${imageUploaded.originalFilename}"
            println "Tamanho: ${imageUploaded.size}"
            println "Tipo: ${imageUploaded.contentType}"
            designInstance.icone = imageUploaded.originalFilename

        }

*/

        designInstance.save flush: true


        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'design.label', default: 'Design'), designInstance.id])
                redirect designInstance
            }
            '*' { respond designInstance, [status: CREATED] }
        }
    }

    def edit(Design designInstance) {
        respond designInstance
    }

    @Transactional
    def update(Design designInstance) {
        if (designInstance == null) {
            notFound()
            return
        }

        if (designInstance.hasErrors()) {
            respond designInstance.errors, view: 'edit'
            return
        }

        designInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Design.label', default: 'Design'), designInstance.id])
                redirect designInstance
            }
            '*' { respond designInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Design designInstance) {

        if (designInstance == null) {
            notFound()
            return
        }

        designInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Design.label', default: 'Design'), designInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'design.label', default: 'Design'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
