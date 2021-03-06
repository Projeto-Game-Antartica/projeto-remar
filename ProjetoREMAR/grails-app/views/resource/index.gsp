<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link type="text/css" rel="stylesheet" href="${resource(dir: "css", file: "card.css")}"/>
    <meta name="layout" content="materialize-layout">
    <style>
        .toast { line-height: 20px !important; }
        .row .cluster p { font-size: 24px; }
    </style>
</head>
<body>
    <div class="row cluster">
        <div class="cluster-header">
            <h4>Gerenciar Jogos</h4>
            <div class="divider"></div>
        </div>
        <div class="row show">
            <main class="cardGames">
                <div class="row">
                    <g:if test="${resourceInstanceList}">
                        <g:render template="developerCards" model="[resourceInstanceList:resourceInstanceList]" />
                    </g:if>
                    <g:else>
                        <p>Você ainda não submeteu nenhum jogo. Nos envie um agora mesmo!</p>
                    </g:else>
                    <div class="col s1 offset-s10">
                        <a href="/resource/create"  name="create"
                           class="btn-floating btn-large waves-effect waves-light my-orange tooltipped modal-trigger"
                           data-tooltip="Adicionar modelo de jogo"><i class="material-icons">add</i></a>
                    </div>
                </div>
            </main>
        </div>
    </div>
    <footer class="row">
        <ul class="pagination">
        </ul>
    </footer>

    <div id="user-details-modal" class="modal">
        %{-- Preenchido pelo Javascript --}%
    </div>

    <g:javascript src="game-index.js"/>
</body>
</html>
