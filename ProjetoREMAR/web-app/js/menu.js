/**
 * Created by lucas on 22/01/16.
 */

$(document).ready(function() {
    $('select').material_select();

    $("#search").keyup(function(){
        _this = this;

        $.each($(".card ").find(".card-name"), function() {
            //console.log($(this).text());
            if($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) == -1)
                $(this).closest('div[class^="card square-cover small"]').hide();
            else
                $(this).closest('div[class^="card square-cover small"]').show();
        });
    });
});