package br.ufscar.sead.loa.remar

import br.ufscar.sead.loa.propeller.Propeller
import grails.plugin.springsecurity.annotation.Secured
import grails.web.JSONBuilder
import groovy.io.FileType
import groovy.json.JsonBuilder
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase;

class DspaceController {

    static allowedMethods = [bitstream: "GET"]
    static scope = "session"

    def dspaceRestService

    def index() {
        def community = dspaceRestService.getMainCommunity()

        def subCommunities = dspaceRestService.listSubCommunitiesExpanded()

        render view: 'index', model:[
                community:community,
                subCommunities:subCommunities,
                restUrl: dspaceRestService.getRestUrl(),
                jspuiUrl: dspaceRestService.getJspuiUrl()
        ]
    }

    def listCollections(){

        def collections = dspaceRestService.listCollectionsExpanded(params.id)

        render view: 'listCollections', model:[
                collections:collections,
                communityName: params.names,
                restUrl: dspaceRestService.getRestUrl()

        ]
    }

    def listItems(){
        def items, metadata, bitstreams
        def linkList = []
        def oldUrl = "/dspace/listCollections/${params.old}?names=${params.names.getAt(0)}"

        items = dspaceRestService.listItems(params.id)
        metadata = items.metadata
        bitstreams = items.bitstreams


        for(int i=0; i<metadata.size(); i++){
            String aux = metadata.get(i).find({it.key == 'dc.identifier.uri' }).value
            String link = "http://200.130.75.21/jspui/handle/" + (aux.split("http://hdl.handle.net/")[1])
            linkList.add(link)
        }


        render view: 'listItems', model:[
                                            items: items,
                                            metadata: metadata,
                                            bitstreams: bitstreams,
                                            communityName: params.names.getAt(0),
                                            collectionName: params.names.getAt(1),
                                            restUrl: dspaceRestService.getRestUrl(),
                                            communityUrl: oldUrl,
                                            linkArray: linkList
                                        ]
    }

    def bitstream(){

        def resp = dspaceRestService.getBitstream(params.id)

        render view: "_modalBody", model: [bitstream: resp, restUrl: dspaceRestService.getRestUrl()]
    }

    def create() {

        print(params)

        def img = new File(servletContext.getRealPath("/images/respondasepuder-banner.png"))
        def resp = dspaceRestService.addBitstreamToItem(params.id,img,"respodasepuder-banner.png", "blslballalba")

        render resp
    }

    def delete(){

          def resp = dspaceRestService.deleteCommunity(params.id)
        //MongoHelper.instance.removeData("resource_dspace","uri","forca")
        render resp

    }

    def overview(){
        def process = Propeller.instance.getProcessInstanceById(params.id, session.user.id as long)

        // cria diretório de tmp no processo e copia os outputs para subpastas nomeadas pelo id das tasks instance
        // do process instance corrente
        def tmpFolder = new File("${servletContext.getRealPath("/data/processes/${process.id}")}/tmp")
        if(!tmpFolder.exists()){
            def ant = new AntBuilder()
            tmpFolder.mkdirs()

            process.completedTasks.each { task ->
                def taskFolder = new File(tmpFolder,task.id as String)
                task.outputs.each {output ->
                    ant.copy(
                            file: output.path,
                            tofile: "${taskFolder}/${output.definition.name}",
                            overwrite: true
                    )
                }
            }
        }
        render view: "overview", model: [process:process]
    }


    /*
    * se o step da task for nulo, entao eh necessário criar um item
    * se o step da task for submit_bitstreams, o item ja foi criado e falta submeter os bitstreams
    * se o step da task for completed então a task ja foi enviada para o dspace
    * */
    def listMetadata() {
        def current_task = Propeller.instance.getTaskInstance(params.taskId, session.user.id as long)
        def resource = Resource.findById(Long.parseLong(current_task.getProcess().getVariable("resourceId")))
        def list = []; //lista e bitstreams

        def dir = new File(servletContext.getRealPath("/data/processes/${current_task.getProcess().id}/tmp/${params.taskId}/"))
        dir.eachFileRecurse (FileType.FILES) {file ->
            list << file
        }

//        if(current_task.getVariable("step") == null){
//           render view: '_itemMetadata', model: [task: current_task,
//                                                 resource: resource]
//        }
//        else{
//            println(params)
//            if(current_task.getVariable("step") == "submit_bitstreams")
//            {
//
//                render view: '_bitMetadata', model: [task: current_task,
//                                                     bitstreams: list]
//
//            }
//        }

        render view: 'listMetadata', model: [task: current_task,
                                              resource: resource,
                                              bitstreams:list ]


    }


    //preview metadata
    def previewMetadata(){
        println(params)

        def json = new JsonBuilder()

        def root = json {
            "citation" params.citation
            "title" params.title
            "abstract" params.description
            "license" params.license

            if(params.author.getClass().isArray()){
                "authors" params.author.collect { [name: it] }
            }else{
                "authors" params.author
            }

            if(params.bit_description.getClass().isArray()){
                "bitstreams" params.bit_description.collect { [description: it] }
            }else{
                "bitstreams" params.bit_description
            }
        }

        println(root)

        render  view: "previewMetadata", model: [metadata: root]
    }

    // create-item
    def createItem(){
        println(params)
        println(form)

        withForm { //submssão esperada
            def metadatas = [], list = [:]
            def itemId = null
            def current_task = Propeller.instance.getTaskInstance(params.taskId, session.user.id as long)
            def resource = Resource.get(current_task.getProcess().getVariable('resourceId'))

            //convert date for pattern expected
            Date date = new Date()
            form.publication_date = date.format('YYYY-MM-dd')

            if(current_task.getVariable("step") == null){ // -> criar item

                current_task.putVariable("step","submit_bitstreams",true)

                session.setAttribute('metadataForm',form) //save form in session

                redirect uri: "/dspace/listMetadata?taskId=${params.taskId}"
            }

        }.invalidToken {
            //sbmissão duplicada do formulário
        }

    }

    def submitBitstream(MetadataForm form){
        def current_task = Propeller.instance.getTaskInstance(params.taskId, session.user.id as long)
        def dir = new File(servletContext.getRealPath("/data/processes/${current_task.getProcess().id}/tmp/${params.taskId}/"))
        def i = 0
        def itemId = current_task.getVariable("itemId")

        println(form)
        params.description.each {
            println(it)
            form.bitstreams.add(it.toString())
        }

        dir.eachFileRecurse (FileType.FILES) {file ->
            def description = null
            if(params.description.getClass().isArray()){
                description = params.description.getAt(i)
                i = i+1
            }else{
                description = params.description
            }
            dspaceRestService.addBitstreamToItem(itemId, file, file.name, description)
        }

        current_task.putVariable("step","preview_metadata",true)

        redirect uri: "/dspace/listMetadata?taskId=${params.taskId}"
    }

    //create item and submit bitstreams for dspace
    def finishDataSending(){
        def metadatas = [], list = [:]
        def itemId = null
        def i = 0
        def current_task = Propeller.instance.getTaskInstance(params.taskId, session.user.id as long)
        def resource = Resource.get(current_task.getProcess().getVariable('resourceId'))
        def dir = new File(servletContext.getRealPath("/data/processes/${current_task.getProcess().id}/tmp/${params.taskId}/"))

        //gerar arquivo de metadados do item
        for(def hash : dspaceRestService.listMetadata){
            if(params.get(hash.key).getClass().isArray()){
                params.get(hash.key).each {
                    def m = [:]
                    m.key = hash.value
                    m.value =  it
                    metadatas.add(m)
                }
            }else{
                def m = [:]
                m.key = hash.value
                m.value =  params.get(hash.key)
                metadatas.add(m)
            }
        }

        list.metadata = metadatas

        if(current_task.getVariable("step") == "preview_metadata"){ // -> criar item
            def resource_dspace = MongoHelper.instance.getCollection("resource_dspace", resource.id)
            resource_dspace.collect{
                it.tasks.each{ task -> //procurando pelo id da coleção que o item será criado
                    if(task.id.toString() == current_task.definition.id.toString()){ //achei a coleção correta
                        itemId = dspaceRestService.newItem(task.collectionId, list)
                    }
                }
            }
            current_task.putVariable("itemId",itemId,true)

            dir.eachFileRecurse (FileType.FILES) {file ->
                def description = null
                if(params.description.getClass().isArray()){
                    description = params.description.getAt(i)
                    i = i+1
                }else{
                    description = params.description
                }
                dspaceRestService.addBitstreamToItem(itemId, file, file.name, description)
            }

            current_task.putVariable("step","completed",true)

            render view: 'overview', model: [process: current_task.getProcess()]
        }
    }


    //inserir no mongo os id da coleção e comunidades referentes ao resource submetido
    def createStructure(Resource resourceInstance){
        def processDefinition = Propeller.instance.getProcessDefinitionByUri(resourceInstance.uri)
        def data = [:]
        def listTasks = []
        def logMsg = "pd-${session.user.username}"

        println(processDefinition.name)

        try{
            data.id = resourceInstance.id
            data.name = resourceInstance.name
            data.uri = resourceInstance.uri
            data.communityId = dspaceRestService.newSubCommunity(null, createCommunityMetadata(resourceInstance))

            for (def taskDefinition : processDefinition.tasks) {
                def t = [:]
                t.id = taskDefinition.id
                t.name = taskDefinition.name
                t.uri = taskDefinition.uri
                t.collectionId = dspaceRestService.newCollection(data.communityId,
                        createCollectionMetadata(taskDefinition))
                listTasks.add(t)
            }

            data.tasks = listTasks
            println(data)

            MongoHelper.instance.addCollection('resource_dspace')
            MongoHelper.instance.insertData('resource_dspace',data)

            log.debug "${logMsg} ENDED: success – 201"
            response.status = 201
            render 201

        }catch (SocketTimeoutException timeout){
            println("Timeout in CreateStructure - ${timeout.message}, ${timeout.cause}")
        }
    }

    //dspace/remove/$id_resource?uri=XXXXXXX
    public removeAll(){
        println(params)
        def resource_dspace = MongoHelper.instance.getCollection("resource_dspace",Long.parseLong(params.id))
        resource_dspace.collect{
            def communityId = it.communityId
            dspaceRestService.deleteCommunity(communityId)
        }
        MongoHelper.instance.removeDataFromUri('resource_dspace',params.uri)

        response.status = 205
        render 205
    }

    private static createCommunityMetadata(Resource resource){
        def json = new JsonBuilder()
        def m = json {
            "name" resource.name.toString()
            "copyrightText" "cc-by-sa"
            "introductoryText" resource.name.toString()
            "shortDescription" resource.description.toString()
            "shortDescription" resource.description.toString()
            "sidebarText" resource.description.toString()
        }
        println(m)
        return m
    }

    private static createCollectionMetadata(def task){
        def json = new JsonBuilder()
        def m = json {
            "name" task.name
            "copyrightText" "cc-by-sa"
            "introductoryText" task.name
            "shortDescription" task.description
            "sidebarText" task.description
        }
        println(m)
        return m
    }

}
