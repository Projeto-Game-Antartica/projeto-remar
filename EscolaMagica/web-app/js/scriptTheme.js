/**
 * Created by matheus on 4/29/15.
 */

window.onload = function(){
    console.log("ok");

    $(".save").click(function() {
        var id = $(this).parent().parent().attr("data-id");

        window.top.location.href = "choose/" + id;
    });


    $(".delete").click(function() {
        var tr = $(this).parent().parent();
        var id = $(tr).attr("data-id");
        var data = { _method: 'DELETE' };

        $.ajax({
            type:'POST',
            data: data,
            url: "delete/" + id,
            success:function(data){
                console.log(data);
                $(tr).remove();
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){}});
    });

    var doors = $(".door");

    $(doors).mouseover(function() {
        var src =  $(this).attr('src');
        console.log(src);
        src = src.replace("sheet1", "sheet0");
        console.log(src);
        $(this).attr("src", src);
    });

    $(doors).mouseout(function() {
        var src =  $(this).attr('src');
        console.log(src);
        src = src.replace("sheet0", "sheet1");
        console.log(src);
        $(this).attr("src", src);
    })

};