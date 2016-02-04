class UrlMappings {

	@SuppressWarnings("GroovyAssignabilityCheck")
    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        // begin index mappings
        "/"(controller:'index')
        "/frame$uri**"(controller: "index", action: "frame")
        // end index mappings

        name login: "/login"(view: "login/auth")
        name signup: "/signup"(controller: "user", action: "create")
        "/signup/success/$id"(controller: "user", action: "signUpSuccess")

        // begin user mappings
        "/user/account/confirm/$token"(controller: 'user',action: 'confirmAccount')
        '/user/newpassword/confirm'(controller: 'user',action: 'createPassword')
        '/user/confirmation'(view: '/static/emailuser')
        name resetPassword: "/user/password/reset"(controller: 'user', action: 'resetPassword')
        // end user mappings

        // begin password mappings
        //noinspection GroovyAssignabilityCheck
        //noinspection GroovyAssignabilityCheck
        name developerForm: "/developer/new"(view:"/static/formDeveloper")
        name infoPage: "/index/info" (view: "index/info")
        // end password mappings

        // begin Process API endpoints
        "/process/task/complete/$taskId"(controller:"process", action:"completeTask")
        "/process/task/resolve/$taskId"(controller:"process", action:"resolveTask")
        "/process/tasks/delegate/$processId"(controller:"process", action:"delegateTasks")
        "/process/tasks/overview/$processId"(controller:"process", action:"chooseUsersTasks")
        "/process/publishOptions/$processId"(controller:"process", action:"publishOptions")

        //begin moodle mappings
        "/moodle/confirm/$hash"(controller: "moodle", action: "confirm")
//        "/moodle/link/$moodleId"
//        "/moodle/unlink/$token"

//        "/process/information/customization/$id"(controller: "process", action: "startCustomization")
        // end Process API endpoints

        // begin Resource API endpoints
        "/resource/review/$id/$status?"(controller:"resource", action:"review")
//        '/resource/customizable'(view: '/resource/custGames')
        '/resource/customizableGames'(controller:"resource", action:"customizableGames")
        "/resource/show/$id"(controller: "resource", action: "show")

        '/exported-resource/publicGames'(controller:"exportedResource", action:"publicGames")
        '/exported-resource/myGames'(controller:"exportedResource", action:"myGames")

        // end Resource API endpoints

        name myProfile: "/my-profile" (controller:"user", action:"myProfile")

        "500"(view:'/error')
	}
}
