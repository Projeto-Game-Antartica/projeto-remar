package br.ufscar.sead.loa.remar

import grails.converters.JSON

class StatsController {

    def ranking() {
        /*
         *  Retorna um JSON com maior pontuação dos jogadores
         *  [[nome, maior pontuação]]
         *
         *  Parâmetros:
         *      groupId            -> identificador do grupo
         *      exportedResourceId -> identificador do recurso exportado
         */

        if (params.groupId && params.exportedResourceId) {

            def group = Group.findById(params.groupId)
            def userGroups = UserGroup.findAllByGroup(group)
            def resourceRanking = MongoHelper.instance.getRanking(params.exportedResourceId as Long)
            def groupRanking = []

            for (o in resourceRanking) {
                if (userGroups.find { it.user.id == o.userId } != null) {
                    groupRanking.add( [User.findById(o.userId).name, o.score] )
                }
            }

            render groupRanking as JSON

        } else {
            // TODO: render erro nos parametros
        }
    }

    def conclusionTime() {
        /*
         *  Retorna um JSON com menor tempo dos jogadores
         *  [[nome, menor tempo]]
         *
         *  Parâmetros:
         *      groupId            -> identificador do grupo
         *      exportedResourceId -> identificador do recurso exportado
         */

        if (params.groupId && params.exportedResourceId) {

            def group = Group.findById(params.groupId)
            def userGroups = UserGroup.findAllByGroup(group)
            def resourceTime = MongoHelper.instance.getGameConclusionTime(params.exportedResourceId as Long)
            def groupTime = []

            for (o in resourceTime) {
                if (userGroups.find { it.user.id == o.userId } != null) {
                    groupTime.add( [User.findById(o.userId).name, o.conclusionTime] )
                }
            }

            render groupTime as JSON

        } else {
            // TODO: render erro nos parametros
        }
    }

    def usersInLevels() {
        /*
         *  Retorna um JSON com quantidade de jogadores por nivel
         *  [[nivel, quantidade]]
         *
         *  Parâmetros:
         *      groupId            -> identificador do grupo
         *      exportedResourceId -> identificador do recurso exportado
         */

        if (params.groupId && params.exportedResourceId) {

            def group = Group.findById(params.groupId)
            def userGroups = UserGroup.findAllByGroup(group)
            def resourceLevels = MongoHelper.instance.getUsersInLevels(params.exportedResourceId as Long)
            def groupUsersLevel = []
            def users

            for (o in resourceLevels) {
                users = userGroups.collectMany { it.user.id in o.usersId ? [it.user.id] : [] }
                groupUsersLevel.add( [o.gameLevelName, users.size()] )
            }

            render groupUsersLevel as JSON

        } else {
            // TODO: render erro nos parametros
        }
    }

/*    def levelsAttempts() {
        *//*
         *  Retorna um JSON com quantidade de tentativas por nivel
         *  [[nivel, tentativas]]
         *
         *  Parâmetros:
         *      groupId            -> identificador do grupo
         *      exportedResourceId -> identificador do recurso exportado
         *//*

        if (params.groupId && params.exportedResourceId) {

            def group = Group.findById(params.groupId)
            def userGroups = UserGroup.findAllByGroup(group)
            def users = userGroups.collect {
                it.user.id
            }
            log.debug(users)
            def resourceLevels = MongoHelper.instance.getLevelsAttempts(params.exportedResourceId as Long, users)

        } else {
            // TODO: render erro nos parametros
        }
    }*/
}
