<%@ page import="br.ufscar.sead.loa.remar.GroupExportedResources" %>
<main class="cardGames">
        <div class="row">
            <g:if test="${myExportedResourcesList.size() == 0}">
                %{--<p>Você ainda não possui nenhum jogo!</p>--}%
            </g:if>
            <g:else>
                <g:each in="${myExportedResourcesList}" var="instance">
                <div id="card${instance.id}" class="col l3 s5">
                    <div  class="card hoverable">
                        <div class="card-image waves-effect waves-block waves-light">
                            <img alt="${instance.name}" class="activator" src="/published/${instance.processId}/banner.png">
                        </div>
                            <div class="card-content">
                                <span style="font-size: 1.3em" class="card-title grey-text text-darken-4 activator truncate" data-category="${instance.resource.category.id}" title="${instance.name}">${instance.name}</span>
                                <div class="divider"></div>
                                <span style="color: dimgrey; font-size: 0.9em" class="center">${instance.resource.category.name}</span>
                                <span style="color: dimgrey; font-size: 0.9em" class="center truncate">Feito por: ${instance.owner.username}</span>
                                <span style="color: dimgrey;" class="center">
                                    <i class="fa fa-globe"></i>
                                    <g:if test="${instance.resource.android}">
                                        <i class="fa fa-android"></i>
                                    </g:if>
                                    <g:if test="${instance.resource.desktop}">
                                        <i class="fa fa-windows"></i>
                                        <i class="fa fa-linux"></i>
                                        <i class="fa fa-apple"></i>
                                    </g:if>
                                    <g:if test="${instance.resource.moodle}">
                                        <i class="fa fa-graduation-cap"></i>
                                    </g:if>
                                </span>
                            </div>
                        <div class="right">
                            <i class="activator material-icons" style="color: black; cursor: pointer">more_vert</i>
                        </div>
                        <div class="card-reveal">
                            <div class="row">
                                <h5 class="card-title grey-text text-darken-4 activator col l12 truncate"><small class="left">Jogar:</small><i class="material-icons right">close</i></h5><br>
                                <div class="col l4">
                                    <a style="font-size: 2em; color: black;" target="_blank" href="/published/${instance.processId}/web" class="tooltipped"  data-position="right" data-delay="50" data-tooltip="Web"><i class="fa fa-globe"></i></a>
                                </div>
                                <g:if test="${instance.resource.desktop}">
                                    <div class="col l4">
                                        <a style="font-size: 2em; color: black;" target="_blank" href="/published/${instance.processId}/desktop/${instance.resource.uri}-linux.zip" class="tooltipped"  data-position="right" data-delay="50" data-tooltip="Linux"><i class="fa fa-linux"></i></a>
                                    </div>
                                    <div class="col l4">
                                        <a style="font-size: 2em; color: black;" target="_blank" href="/published/${instance.processId}/desktop/${instance.resource.uri}-windows.zip" class="tooltipped"  data-position="right" data-delay="50" data-tooltip="Windows"><i class="fa fa-windows"></i></a> <br>
                                    </div>
                                    <div class="col l4">
                                        <a style="font-size: 2em; color: black;" target="_blank" href="/published/${instance.processId}/desktop/${instance.resource.uri}-mac.zip" class="tooltipped"  data-position="right" data-delay="50" data-tooltip="Mac"><i class="fa fa-apple"></i></a> <br>
                                    </div>
                                </g:if>

                                <div class="col l4">
                                    <g:if test="${instance.resource.android}">
                                        <a style="font-size: 2em; color: black;" target="_blank" href="/published/${instance.processId}/mobile/${instance.resource.uri}-android.zip" class="tooltipped"  data-position="right" data-delay="50" data-tooltip="Android"><i class="fa fa-android"></i></a> <br>
                                    </g:if>
                                </div>
                                <div class="col l4">
                                    <g:if test="${instance.resource.moodle}">
                                        <a style="font-size: 2em; color: black;" class="tooltipped"  data-position="right" data-delay="50" data-tooltip="Disponível no Moodle"><i class="fa fa-graduation-cap"></i></a>
                                    </g:if>
                                </div>
                            </div>
                            <div class="divider"></div><br>
                            <div class="row">
                                <div class="center">
                                    <div class="col l4">
                                        <a style="font-size: 2em;" href="/exported-resource/info/${instance.id}"
                                           class="tooltipped"  data-position="down" data-delay="50" data-tooltip="Mais informações">
                                            <i class="fa fa-info-circle" style="color: #FF5722;"></i>
                                        </a>
                                    </div>
                                    
                                    <div class="col l4">
                                        <a style="font-size: 2em;" onclick="deleteResource(${instance.id})"
                                           class="tooltipped"  data-position="down" data-delay="50" data-tooltip="Excluir">
                                            <i class="fa fa-trash" style="color: #FF5722;"></i>
                                        </a>
                                    </div>
                                    <g:if test="${instance.resource.shareable}">
                                        <div class="col l4">
                                            <a style="font-size: 2em;" href="#modal-group-${instance.id}" class="tooltipped modal-trigger" data-position="down" data-delay="50" data-tooltip="Compartilhar em grupo">
                                                <i class="fa fa-users" style="color: #FF5722;"></i>
                                            </a>
                                        </div>
                                    </g:if>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
                    <div id="modal-group-${instance.id}" class="modal col l6 offset-l3 s12">
                        <div class="modal-content">
                            <ul class="collection with-header">
                                <g:if test="${!myGroups.empty}"> <li class="collection-header"><h5>Meus grupos disponíveis</h5></li></g:if>
                                <g:else> <li class="collection-header"><h5>Você não possui um grupo</h5></li> </g:else>
                                <g:each var="group" in="${myGroups}">
                                    <li class="collection-item">
                                        <div>
                                            <p>${group.name}</p>
                                            <p>
                                                Dono: ${group.owner.firstName + " " + group.owner.lastName}<br>
                                            </p>
                                        </div>
                                        <g:if test="${!GroupExportedResources.findByGroupAndExportedResource(group,instance)}">
                                            <input name="groupsid" id="groups-${group.id}-instance-${instance.id}" value="${group.id}" type="checkbox">
                                        </g:if>
                                        <g:else>
                                            <input name="groupsid2"  checked="checked" disabled="disabled" type="checkbox">
                                        </g:else>
                                        <label style="position:relative; bottom: 2em;" for="groups-${group.id}-instance-${instance.id}" class="secondary-content"></label>
                                    </li>
                                </g:each>
                                <g:if test="${!groupsIAdmin.empty}">
                                    %{--<li class="collection-item"></li>--}%
                                    <li class="collection-header"><h5>Grupos que administro</h5></li>
                                </g:if>
                                <g:else> <li class="collection-header"><h5>Você não administra nenhum grupo</h5></li></g:else>
                                <g:each var="group" in="${groupsIAdmin}">
                                    <li class="collection-item">
                                        <div>
                                            <p>${group.name}</p>
                                            <p>
                                                Dono: ${group.owner.firstName + " " + group.owner.lastName}<br>
                                            </p>
                                        </div>
                                        <g:if test="${!GroupExportedResources.findByGroupAndExportedResource(group,instance)}">
                                            <input name="groupsid" id="groups-${group.id}-instance-${instance.id}" value="${group.id}" type="checkbox">
                                        </g:if>
                                        <g:else>
                                            <input name="groupsid2"  checked="checked" disabled="disabled" type="checkbox">
                                        </g:else>
                                        <label style="position:relative; bottom: 2em;" for="groups-${group.id}-instance-${instance.id}" class="secondary-content"></label>
                                    </li>
                                </g:each>
                                <input type="hidden" name="exportedresource" value="${instance.id}">
                                <div class="row">
                                    <button data-instance-id="${instance.id}" style=" top: 0.8em; right: -1.2em; position:relative;" class="btn waves-effect waves-light" type="submit" name="action">Compartilhar
                                        <i class="material-icons right">send</i>
                                    </button>
                                </div>
                            </ul>
                        </div>
                    </div>
                </g:each>
            </g:else>
        </div>
        <g:javascript src="add-resource-to-group.js" />
        <g:applyLayout name="pagination"/>
    </main>

