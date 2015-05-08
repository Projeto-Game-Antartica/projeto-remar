/**
 * Created by matheus on 5/7/15.
 */

window.addEventListener("load", function() {

    $("#send").on("click", function() {
        $(".endpoint").each(function() {
            var checkbox = $(this).find("input");
            if(checkbox[0].checked) {
                $(this).removeClass("endpoint");
                var id = this.id;
                var intervalId = setInterval(function(){etc(id)}, 500);
                ajax(id, intervalId);
            }
        });
    });
});

function ajax(endpoint, intervalId) {

    $.ajax({
        type:'GET',
        url: endpoint,
        success:function(data){
            clearInterval(intervalId);
            $("#" + endpoint).find("h1").html("Versão web: <a target=\"_blank\" href=\"" + location.origin + data + "\">clique aqui</a>");
        },
        error:function(XMLHttpRequest,textStatus,errorThrown){}});
}

function etc(id) {
    var el = $("#" + id).find("h1");
    var html = $(el).html();

    if(html != "Processando..." && html.indexOf("Processando") > -1) {
        html += ".";
    } else if (!html.indexOf("Processando") > -1 || html.indexOf("...") > -1) {
        html = "Processando.";
    }

    $(el).html(html);

}