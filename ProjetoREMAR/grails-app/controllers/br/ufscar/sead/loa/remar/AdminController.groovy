package br.ufscar.sead.loa.remar

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import grails.converters.JSON
import org.apache.commons.lang.RandomStringUtils
import org.springframework.web.multipart.commons.CommonsMultipartFile
import static org.springframework.http.HttpStatus.*

@Secured(["ROLE_ADMIN"])
class AdminController {

    def springSecurityService
    static allowedMethods = [deleteUser: "POST"]

    def index() {
        render view: "dashboard"

    }

    def users() {
        def users = User.list().sort {it.getName().toLowerCase()}
        render view: "users", model: [users: users]
    }

    def groups() {
        def groups = Group.list().sort {it.name.toLowerCase()}
        render view: "groups", model: [groups: groups]
    }

    def categories() {
        def categories = Category.list().sort {it.name.toLowerCase()}
        render view: "categories", model: [categories: categories]
    }

    def games() {
        def games = ExportedResource.list().sort {it.getName().toLowerCase()}
        render view: "games", model: [games: games]
    }

    def deleteUser() {
        deleteUserProc(params.id)
        render (status: 200)
    }

    def deleteGroup() {
        deleteGroupProc(params.id)
        render (status: 200)
    }

    def deleteCategory() {
        deleteCategoryProc(params.id)
        render (status: 200)
    }

    def deleteGame() {
        deleteGameProc(params.id)
        render (status: 200)
    }


    def deleteUserBatch() {
        def userIdList = JSON.parse(params.userIdList)

        userIdList.each {
            deleteUserProc(it)
        }

        render (status: 200)
    }

    def deleteGroupBatch() {
        def groupIdList = JSON.parse(params.groupIdList)

        groupIdList.each {
            deleteGroupProc(it)
        }

        render (status: 200)
    }

    def deleteCategoryBatch() {
        def categoryIdList = JSON.parse(params.categoryIdList)

        def categoryRemoved = []

        categoryIdList.each {
            def resource = Resource.findByCategory(Category.findById(it))

            if (!resource) {
                deleteCategoryProc(it)
                categoryRemoved.add(it)
            }
        }

        render categoryRemoved as JSON
    }

    def deleteGameBatch() {
        def gameIdList = JSON.parse(params.gameIdList)

        gameIdList.each {
            deleteGameProc(it)
        }

        render (status: 200)
    }

    @Transactional
    saveCategory() {
        Category category = new Category(params)

        if (category == null) {
            notFound()
            return
        }

        category.save flush:true

        render category as JSON
    }

    @Transactional
    editCategory(Category categoryInstance) {

        if (categoryInstance == null) {
            render(status: 410, text: "ERROR: Failed editing category")
            return
        }

        categoryInstance.name = params.name

        categoryInstance.save flush:true
        render (status: 200)
    }

    @Transactional
    toggleUserDeveloperStatus() {
        def user = User.findById(params.id)
        def role = Role.findByAuthority("ROLE_DEV")
        def userRole = UserRole.findByUserAndRole(user, role)

        if (!userRole) {
            UserRole.create(user, Role.findByAuthority("ROLE_DEV"), true)

            render true
            return
        } else {
            userRole.delete flush: true

            render false
            return
        }

    }

    /**
     * @desc   register a batch of new users from a spreadsheet file
     *         .csv, .xls and .xlxs files are accepted
     * @param  file - the file from which users will be imported
     * @return a message if something goes wrong, otherwise a redirect
     */
    @Transactional
    importUsers() {
        CommonsMultipartFile file = request.getFile("spreadsheet-file")

        if (!file.empty) {
            switch (file.contentType) {
                case "application/vnd.ms-excel":
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                    ExcelUtil converter = new ExcelUtil()
                    converter.getObjectsFromExcelFile(file).each {
                        registerUser(it[0] /* username */, it[1] /* email */, it[2] /* firstName */, it[3] /* lastName */)
                    }
                    break
                case "text/csv":
                    Util.readCSV(file, ';', 'UTF-8').each {
                        registerUser(it[0] /* username */, it[1] /* email */, it[2] /* firstName */, it[3] /* lastName */)
                    }
                    break
                default:
                    flash.message = g.message(code: "admin.users.import.error.contentType")
                    break
            }
        } else {
            flash.message = g.message(code: "default.errors.fileEmpty")
        }

        redirect action: "users"
    }

    /**
     * @desc    export selected users to a .csv, .xls or .xlsx file
     * @params  params - an object containing parameters from ajax call
     * @return  path to download file
     */
    def exportUsers() {
        def date = new Date()
        final String extension = params.ext
        final String filename = "exportedUsers_" + date.format("yyyyMMdd") + "." + extension
        final String encoding = grailsApplication.config.grails.converters.encoding ?: "UTF-8"
        final String mimeType = grailsApplication.config.grails.mime.types[extension] ?: "text/csv"

        try {
            response.setStatus(OK.value())
            response.setContentType("${mimeType};charset=${encoding}")
            response.setHeader("Content-Disposition", "attachment;filename=${filename}")

            switch (mimeType) {
                case "application/vnd.ms-excel":
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                    def dataToWrite = []

                    User.getAll(JSON.parse(params.userIdList)).each {
                        def row = [
                                it.username,
                                it.email,
                                it.firstName,
                                it.lastName
                        ]

                        dataToWrite.add(row)
                    }

                    def converter = new ExcelUtil()
                    def workbook = converter.writeListToExcelFile(dataToWrite, extension)

                    workbook.write(response.outputStream)
                    break
                case "text/csv":
                    User.getAll(JSON.parse(params.userIdList)).each {
                        def row = it.username + ";" +
                                it.email + ";" +
                                it.firstName + ";" +
                                it.lastName + "\n"

                        response.outputStream << row
                    }
                    break
                default:
                    flash.message = g.message(code: "admin.users.import.error.contentType")
                    break
            }
        } catch (e) {
            log.debug(e + " in Admin/ExportCSV")
        }

        response.outputStream.flush()
        response.outputStream.close()
    }

    /**
     * @desc   Procedure to remove an user and all data related to him
     * @param  id - the id from the user to be removed
     * @return nothing
     */
    @Transactional
    deleteUserProc(id) {
        def userInstance = User.findById(id)

        if (userInstance == null) {
            render(status: 410, text: "ERROR: Failed removing user")
            return
        }

        UserRole.removeAll(userInstance, true)

        // Remove Tokens that belong to the user
        List<Token> tokens = Token.findAllByOwner(userInstance)
        for (int i = 0; i < tokens.size(); i++)
            tokens.get(i).delete()

        // Remove Resources that belong to the user
        List<Resource> resources = Resource.findAllByOwner(userInstance)
        for (int i = 0; i < resources.size(); i++)
            resources.get(i).delete()

        // Remove Exported Resources that belong to the user
        List<ExportedResource> exportedResources = ExportedResource.findAllByOwner(userInstance)
        for (int i = 0; i < exportedResources.size(); i++)
            exportedResources.get(i).delete()

        // Remove groups that belong to the user
        List<Group> groups = Group.findAllByOwner(userInstance)
        for (int i = 0; i < groups.size(); i++)
            groups.get(i).delete()

        // Remove user-group relationships
        UserGroup.removeAllByUser(userInstance, true)

        userInstance.delete flush: true
    }

    @Transactional
    deleteGroupProc(id) {
        def groupInstance = Group.findById(id)

        if (groupInstance == null) {
            render(status: 410, text: "ERROR: Failed removing group")
            return
        }
    }

    /**
     * @desc   Procedure to remove an category
     * @param  id - the id from the category to be removed
     * @return nothing
     */
    @Transactional
    deleteGameProc(id) {
        def gameInstance = ExportedResource.findById(id)

        if (gameInstance == null) {
            render(status: 410, text: "ERROR: Failed removing game")
            return
        }

        gameInstance.delete flush: true
    }

    /**
     * @desc   Procedure to remove an category
     * @param  id - the id from the category to be removed
     * @return nothing
     */
    @Transactional
    deleteCategoryProc(id) {
        def categoryInstance = Category.findById(id)

        if (categoryInstance == null) {
            render(status: 410, text: "ERROR: Failed removing category")
            return
        }

        categoryInstance.delete flush: true
    }

    /**
     * @desc   create an user with randomly generated password and send an email prompting to change that password
     * @param  username - created user username
     * @param  email - created user email
     * @param  firstName - created user first name
     * @param  lastName - created user last name
     * @return nothing
     */
    def registerUser(String username, String email, String firstName, String lastName) {
        User user = new User()
        user.username = username
        user.email = email
        user.firstName = firstName
        user.lastName = lastName
        user.password = RandomStringUtils.random(8, true, true)

        user.save flush: true

        if (user.hasErrors()) {
            log.debug(user.errors)
        } else {
            def html = g.render(template: "newUserEmail", model: ["user": user])
            Util.sendEmail(email, "Bem-vindo ao REMAR", html)
        }
    }
}