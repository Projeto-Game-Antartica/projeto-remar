package br.ufscar.sead.loa.remar

/**
 * Created by matheus on 12/1/15.
 */
class Util {
    def static later(code) {
        def r = new Runnable() {
            @Override
            void run() {
                code()
            }
        }
        new Thread(r).start()
    }
}
