/**
 * Created by leticia on 12/09/16.
 */
$(document).ready(function() {
    $('select').material_select();
});

window.onload = function() {

    $("#submitButton").click(function() {
        if ($("#palavra11").val() != "" &&
            $("#palavra12").val() != "" &&
            $("#palavra13").val() != "" &&
            $("#palavra1").val() != "" &&
            $("#palavra21").val() != "" &&
            $("#palavra22").val() != "" &&
            $("#palavra23").val() != "" &&
            $("#palavra2").val() != "" &&
            $("#palavra31").val() != "" &&
            $("#palavra32").val() != "" &&
            $("#palavra33").val() != "" &&
            $("#palavra3").val() != "") {

            var palavra11 = $("#palavra11").val();
            var palavra12 = $("#palavra12").val();
            var palavra13 = $("#palavra13").val();
            var palavra1 = $("#palavra1").val();
            var palavra21 = $("#palavra21").val();
            var palavra22 = $("#palavra22").val();
            var palavra23 = $("#palavra23").val();
            var palavra2 = $("#palavra2").val();
            var palavra31 = $("#palavra31").val();
            var palavra32 = $("#palavra32").val();
            var palavra33 = $("#palavra33").val();
            var palavra3 = $("#palavra3").val();

            $.ajax({
                type: "POST",
                traditional: true,
                url: "/santograu/faseBiblioteca/exportLevel",

                data: { palavra1dica1:palavra11, palavra1dica2:palavra12, palavra1dica3:palavra13, palavra1:palavra1,
                        palavra2dica1:palavra21, palavra2dica2:palavra22, palavra2dica3:palavra23, palavra2:palavra2,
                        palavra3dica1:palavra31, palavra3dica2:palavra32, palavra3dica3:palavra33, palavra3:palavra3},

                success: function (returndata) {
                    window.top.location.href = returndata;
                },
                error: function (returndata) {
                    alert("Error:\n" + returndata.responseText);
                    if(returndata.status == 401) {
                        var url = document.referrer;
                        window.top.location.href = url
                    } else {
                        alert("Error:\n" + returndata.responseText);
                    }
                }
            });

        } else {
            $("#errorSubmitingModal").openModal();
        }
    })
}
