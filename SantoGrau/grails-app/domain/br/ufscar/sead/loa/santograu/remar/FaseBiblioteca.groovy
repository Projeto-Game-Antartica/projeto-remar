package br.ufscar.sead.loa.santograu.remar

class FaseBiblioteca {

    String[] palavra1 = newString[4]
    String[] palavra2 = newString[4]
    String[] palavra3 = newString[4]

    long ownerId
    String taskId

    static constraints = {
        ownerId blank: false, nullable: false
        taskId nullable: true
        palavra1 blank: false, nullable: false
        palavra2 blank: false, nullable: false
        palavra3 blank: false, nullable: false
    }
}