
<%@ page import="br.ufscar.sead.loa.santograu.remar.FaseBiblioteca" %>
<!DOCTYPE html>
<html>
<head>
    <title>Fase Biblioteca</title>
    <meta name="layout" content="main">
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <g:javascript src="faseBiblioteca.js" />
    <g:external dir="css" file="faseBiblioteca.css"/>
    <g:javascript src="iframeResizer.contentWindow.min.js"/>

    <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
</head>
<body>
<div class="cluster-header">
    <p class="text-teal text-darken-3 left-align margin-bottom" style="font-size: 28px;">
        Customização - Fase Biblioteca
    </p>
</div>
<div id="list-faseTecnologia" class="content scaffold-list row" role="main">
    <div class="form" id="myForm" >
        <g:render template="form"/>
    </div>

</div>
</body>
</html>
