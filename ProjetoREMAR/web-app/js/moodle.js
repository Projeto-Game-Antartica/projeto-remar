/**
 * Created by loa on 31/07/15.
 */
window.onload = function () {

        $("#moodle").click(function () {
            $('#moodleForm').toggle($(':checkbox:checked').length > 0);
            console.log("Ola");

        });


    }

