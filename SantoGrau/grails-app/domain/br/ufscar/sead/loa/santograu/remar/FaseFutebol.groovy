package br.ufscar.sead.loa.santograu.remar

class FaseFutebol {

        String title //enunciado da questão
        String correctAnswer //resposta certa

        long ownerId
        String taskId

        static constraints = {
            ownerId blank: false, nullable: false
            taskId nullable: true
        }
}
