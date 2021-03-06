/**
 * Created by Douglas on 22/09/17.
 * Based on Santo Grau models (faseCampoMinado and faseTCC)
 */

var list_id_delete = [];

window.onload = function(){
    //$("#title").characterCounter();
    $('.modal-trigger').leanModal();
    /*$("#SearchLabel").keyup(function(){
        _this = this;
        $.each($("#table tbody ").find("tr"), function() {
            if($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) == -1)
                $(this).hide();
            else
                $(this).show();
        });
    });*/

    if($("#errorImportingQuestions").val() == "true") {
        $("#errorImportingQuestionsModal").openModal();
    }
};

function _modal_edit(tr){
    var url = location.origin + '/sanjarunner/quizSantos/returnInstance/' + $(tr).attr('data-id');
    var data = {_method: 'GET'};

    $.ajax({
            type: 'GET',
            data: data,
            url: url,
            success: function (returndata) {
                var quizSantosInstance = returndata.split("%@!");
                //quizSantosInstance é um vetor com os atributos da classe Question na seguinte ordem:
                // Question - Answer[0] - Answer[1] - Answer[2] - Answer[3] - CorrectAnswer - ID

                switch(quizSantosInstance[5]){
                    case '0':
                        $("#editRadio0").attr("checked","checked");
                        break;
                    case '1':
                        $("#editRadio1").attr("checked","checked");
                        break;
                    case '2':
                        $("#editRadio2").attr("checked","checked");
                        break;
                    case '3':
                        $("#editRadio3").attr("checked","checked");
                        break;
                    default :
                        console.log("Alternativa correta inválida");
                }
                $("#editQuestion").attr("value",quizSantosInstance[0]);
                $("#labelQuestion").attr("class","active");
                $("#labelAnswers1").attr("class","active");
                $("#labelAnswers2").attr("class","active");
                $("#labelAnswers3").attr("class","active");
                $("#labelAnswers4").attr("class","active");
                $("#editAnswers0").attr("value",quizSantosInstance[1]);
                $("#editAnswers1").attr("value",quizSantosInstance[2]);
                $("#editAnswers2").attr("value",quizSantosInstance[3]);
                $("#editAnswers3").attr("value",quizSantosInstance[4]);
                $("#quizSantosID").attr("value",quizSantosInstance[6]);
                $("#editModal").openModal();
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log("Error, não retornou a instância");
                if(returndata.status == 401) {
                    var url = document.referrer;
                    //url = url.substr(0,url.indexOf('/',7))
                    window.top.location.href = url //+ "/login/auth"
                } else {
                    alert("Error:\n" + returndata.responseText);
                }
            }
        }
    );
}

function exportQuestions(){
    var list_id = [];

    //cria uma lista com os ids dos textos
    $.each($("[class=selectable_tr]"), function (ignored, el) {
        var tr = $(el);
        console.log(tr);
        list_id.push($(tr).attr('data-id'));
    });

    $.ajax({
        type: "POST",
        traditional: true,
        url: "/sanjarunner/quizSantos/exportCSV",
        data: { list_id: list_id },
        success: function(returndata) {
            console.log(returndata);
            window.open(location.origin + returndata, '_blank');
        },
        error: function(returndata) {
            if(returndata.status == 401) {
                var url = document.referrer;
                //url = url.substr(0,url.indexOf('/',7))
                window.top.location.href = url //+ "/login/auth"
            } else {
                alert("Error:\n" + returndata.responseText);
            }
        }
    });
}

function _submit() {
    var list_id = [];

    //cria uma lista com os ids de cada questao
    $.each($("[class=selectable_tr]"), function (ignored, el) {
        var tr = $(el);
        console.log(tr);
        list_id.push($(tr).attr('data-id'));
    });

    //chama o controlador para criar o arquivo txt com as questoes inseridas
    $.ajax({
        type: "POST",
        traditional: true,
        url: "/sanjarunner/quizSantos/exportQuestions",
        data: { list_id: list_id},
        success: function(returndata) {
            window.top.location.href = returndata;
        },
        error: function(returndata) {
            if(returndata.status == 401) {
                var url = document.referrer;
                //url = url.substr(0,url.indexOf('/',7))
                window.top.location.href = url //+ "/login/auth"
            } else {
                alert("Error:\n" + returndata.responseText);
            }
        }
    });
}