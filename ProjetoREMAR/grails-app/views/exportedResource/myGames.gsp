<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Meus jogos</title>
    <meta name="layout" content="materialize-layout">
</head>

<body>
<div class="row cluster">
    <div class="cluster-header">
        <h4>Meus Jogos</h4>
        <div class="divider"></div>
    </div>
    <div class="row">
        <div class="col s12">
            <ul class="tabs">
                <g:if test="${mode == "1"}">
                    <li class="tab col s3"><a class="active" href="#test1">Publicados</a></li>
                    <li class="tab col s3"><a href="#test2">Em customização</a></li>
                </g:if>
                <g:else>
                    <li class="tab col s3"><a href="#test1">Publicados</a></li>
                    <li class="tab col s3"><a class="active" href="#test2">Em customização</a></li>
                </g:else>
            </ul>
        </div>
        <section id="test1" class="col s12"> <!-- start my published games -->
            <div class="row search">
                <div class="input-field col s6">
                    <input id="search" type="text" placeholder="Buscar meus jogos"class="validate">
                    <label for="search"><i class="fa fa-search" data-tooltip="Buscar"></i></label>
                </div>
                <div class="input-field col s6">
                    <select class="material-select">
                        <option class="option" value="-1" selected>Todas</option>
                        <g:if test="${categories.size() > 0}">
                            <g:each in="${categories}" var="category">
                                <option class="option" value="${category.id}">${category.name}</option>
                            </g:each>
                        </g:if>
                    </select>
                    <label>Categoria</label>
                </div>
            </div>
            <div id="showCards" class="row">
                <article class="row">
                    <g:render template="customizedGameCard" model="[publicExportedResourcesList:myExportedResourcesList]" />
                </article>
            </div>
        </section> <!-- finished my published games-->

        <section id="test2" class="col s12"> <!-- start processes-->
            <div class="row search">
                <div class="input-field col s12">
                    <input id="search-processes" type="text" class="validate">
                    <label for="search-processes">
                        <i class="fa fa-search" data-tooltip="Buscar"></i>
                    </label>
                </div>
            </div>
            <div id="showCardsProcess" class="row">
                <article class="row">
                    <g:render template="/process/process" model="[processes:processes]" />
                </article>
            </div>
        </section> <!-- finished processes-->
    </div>
</div>

<div id="userDetailsModal" class="modal remar-modal">
    %{-- Preenchido pelo Javascript --}%
</div>

<g:javascript src="remar/menu.js"/>
</body>
</html>
