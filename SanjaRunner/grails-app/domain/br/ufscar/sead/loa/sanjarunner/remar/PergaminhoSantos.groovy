package br.ufscar.sead.loa.sanjarunner.remar

class PergaminhoSantos {

    String[] information = new String[5]

    static constraints = {
        information (blank : false, size: 1..100)
    }
}
