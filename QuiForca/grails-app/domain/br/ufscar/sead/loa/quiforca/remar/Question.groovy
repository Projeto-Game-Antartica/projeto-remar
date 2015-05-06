package br.ufscar.sead.loa.quiforca.remar

class Question {

    String statement
    String answer
    String category
    String author

    long ownerId

    static constraints = {
        statement blank: false
        answer blank: false
        category blank: true
        author blank: false
        ownerId blank: false, nullable: false
    }
}