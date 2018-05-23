$(document).ready(function() {
  $('select').material_select();
});

window.onload = function() {

  $("#submitButton").click(function() {
    if ($("#title").val() != "" && $("#correctAnswer").val() != "") {

      var title = $("#title").val();
      var correctAnswer = $("#correctAnswer").val();

      //Chama controlador para salvar questões em arquivos .json
      $.ajax({
        type: "POST",
        traditional: true,
        url: "/santograu/faseFutebol/exportLevel",
        data: {
          title: title,
          correctAnswer: correctAnswer
        },
        success: function(returndata) {
          window.top.location.href = returndata;
        },
        error: function(returndata) {
          alert("Error:\n" + returndata.responseText);
          if (returndata.status == 401) {
            var url = document.referrer;
            //url = url.substr(0,url.indexOf('/',7))
            window.top.location.href = url //+ "/login/auth"
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
