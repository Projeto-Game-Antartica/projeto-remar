<%--
Created by IntelliJ IDEA.
User: lucasbocanegra
Date: 07/06/16
Time: 08:58
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="materialize-layout">
    <title>Repositório</title>
</head>
<body>
    <div class="row cluster">
        <div class="cluster-header">
            <p id="title-page" class="text-teal text-darken-3 left-align margin-bottom title-page">
                <i class="medium material-icons left">cloud</i>Repositório
            </p>
            <div class="divider"></div>
            <article class="width-position left-align">
                <section class="row">
                    <div class="col s12">
                        <aside class="nav-breadcrumb">
                            <div class="nav-wrapper">
                                <p class="first-breadcrumb active">Comunidades</p>
                            </div>
                        </aside>
                        <div class="card-content text-justify">
                            <blockquote>Neste espaço estão disponíveis alguns artefatos customizados por nossos usuários e
                                usados na criação dos jogos. Tais artefatos encontram-se no Dspace
                                (<a href="${jspuiUrl}" target="_blank">${jspuiUrl}</a>).
                                Este espaço faz uma abstração dos artefatos lá encontrados. Eles estão divididos em
                                comunidades, nomeadas pelo nome de cada jogo, coleções e os items de cada coleção.
                                O usuário pode baixar o artefato por este espaço e usá-lo, por exemplo, para customizar
                                um jogo.
                            </blockquote>
                        </div>
                    </div>
                </section>
                <section class="row">
                    <div class="col s12" >
                        <ul class="collection">
                            <g:each in="${subCommunities}" var="community">
                                <li class="collection-item avatar left-align">
                                    <g:if test="${community.retrieveLink != null}">
                                        <img src="${restUrl}${community.retrieveLink}" alt="" class="circle">
                                    </g:if>
                                    <a href="/dspace/repository/${community.id}" class="collection-link" >${community.name}</a>
                                    %{--<g:link class="collection-link" action="listCollections" params="[communityId: community.id]">--}%
                                        %{--${community.name}--}%
                                    %{--</g:link>--}%
                                    <p>${community.shortDescription}</p>
                                </li>
                            </g:each>
                        </ul>
                    </div>
                </section>
            </article>
        </div>
    </div>
</body>
</html>