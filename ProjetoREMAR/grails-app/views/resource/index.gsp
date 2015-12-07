<%--
  Created by IntelliJ IDEA.
  User: loa
  Date: 25/06/15
  Time: 11:04
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="materialize-layout">
</head>
<body>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'game-index.js')}"></script>
    <script>
        $(document).on('change', '.btn-file :file', function() {
            var input = $(this),
                    numFiles = input.get(0).files ? input.get(0).files.length : 1,
                    label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
            input.trigger('fileselect', [numFiles, label]);
        });

        $(document).ready( function() {
            $('.btn-file :file').on('fileselect', function(event, numFiles, label) {

                var input = $(this).parents('.input-group').find(':text'),
                        log = numFiles > 1 ? numFiles + ' files selected' : label;

                if( input.length ) {
                    input.val(log);
                } else {
                    if( log ) alert(log);
                }

            });
        });
    </script>



<div class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="box box-body box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">
                        <i class="fa fa-archive"></i>
                        Submeter R.E.A.
                    </h3>
                </div><!-- /.box-header -->
                <div class="box-body">

                    <div class="row">
                        <div class="direct-chat-messages page-size" >
                            <div class="widget-content-white glossed">
                                <div class="padded">
                                    <div class="row">

                                        <g:each in="${resourceInstanceList}" status="i" var="gameInstance">
                                            %{--${gameInstance.status}--}%
                                            <div class="col-md-3">
                                                <g:if test="${gameInstance.status == 'pending'}">
                                                    <div class="info-box bg-yellow-gradient">
                                                </g:if>
                                                <g:elseif test="${gameInstance.status == 'approved'}">
                                                    <div class="info-box bg-green-gradient">
                                                </g:elseif>
                                                <g:elseif test="${gameInstance.status == 'rejected'}">
                                                    <div class="info-box bg-red-gradient">
                                                </g:elseif>

                                                <span class="info-box-icon">
                                                    %{--<a href="/process/start/${gameInstance.bpmn}" target="_self">                                                           --}%
                                                    %{--<i class="fa fa-magic"></i>--}%
                                                    %{--</a>--}%

                                                    <img src="/images/${gameInstance.uri}-banner.png"
                                                         class="img img-responsive center-block"/>
                                                </span>

                                                <div class="info-box-content">
                                                    <div class="pull-right">
                                                        <div class="dropdown pointer text-center">
                                                            <div class="dropdown-toggle" data-toggle="dropdown" style="min-width: 10px;">
                                                                <i class="fa fa-ellipsis-v"></i>
                                                            </div>
                                                            <ul class="dropdown-menu">
                                                                <sec:ifAllGranted roles="ROLE_ADMIN">
                                                                    <li><a class="review" data-review="approve" data-id="${gameInstance.id}">Aprovar</a></li>
                                                                    <li><a class="review" data-review="reject" data-id="${gameInstance.id}">Rejeitar</a></li>
                                                                    <li class="divider"></li>
                                                                </sec:ifAllGranted>
                                                                <li><a class="delete" data-id="${gameInstance.id}">Excluir</a></li>
                                                            </ul>
                                                        </div>
                                                    </div>

                                                    <span class="info-box-text">${gameInstance.name.toUpperCase()}</span>
                                                    <span id="development" class="info-box-number">
                                                        <sec:ifAllGranted roles="ROLE_ADMIN">
                                                            <input class="form-control comment" data-id="${gameInstance.id}" type="text" placeholder="Comment" value="${gameInstance.comment}">
                                                        </sec:ifAllGranted>
                                                        <sec:ifNotGranted roles="ROLE_ADMIN">
                                                            ${gameInstance.comment}
                                                        </sec:ifNotGranted>
                                                    </span>

                                                    <span class="progress-description">
                                                        <div class="pull-right">
                                                            <i class="fa fa-at"></i>
                                                            <g:if test="${gameInstance.android}">
                                                                <i class="fa fa-android"></i>
                                                            </g:if>
                                                            <g:if test="${gameInstance.linux}">
                                                                <i class="fa fa-linux"></i>
                                                            </g:if>
                                                            <g:if test="${gameInstance.moodle}">
                                                                <i class="fa fa-graduation-cap"></i>
                                                            </g:if>
                                                        </div>
                                                    </span>
                                                </div><!-- /.info-box-content -->
                                                </div><!-- /.info-box -->
                                            </div>

                                        </g:each>
                                        %{--<a href="/resource/create">--}%
                                        <a href="" data-toggle="modal" data-target="#myModal">

                                            <div class="col-md-3">
                                                <div class="info-box bg-info" style="color: dimgray;">
                                                    <span class="info-box-icon">
                                                        <i class="fa fa-upload"></i>
                                                    </span>

                                                    <div class="info-box-content">
                                                        <span class="info-box-text">Novo R.E.A.</span>
                                                        <span id="development" class="info-box-number">
                                                            <div style="height: 30px;"></div>
                                                        </span>

                                                        <span class="progress-description">
                                                            <div class="pull-right">
                                                                <i class="fa fa-at"></i>
                                                                <i class="fa fa-android"></i>
                                                                <i class="fa fa-linux"></i>
                                                                <i class="fa fa-graduation-cap"></i>
                                                            </div>
                                                        </span>

                                                    </div><!-- /.info-box-content -->
                                                </div><!-- /.info-box -->
                                            </div>
                                        </a>
                                    </div>
                                    </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal -->
    <div class="modal" id="myModal" role="dialog">
        <div class="modal-dialog">

            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">
                        <i class="fa fa-upload"></i>
                        Novo R.E.A.
                    </h4>
                </div>
                <div class="modal-body">
                    <g:form class="" url="[resource:gameInstance, action:'save']" enctype="multipart/form-data" useToken="true">

                        <div class="form-group has-feedback" >
                            <div class="input-group">
                                <span class="input-group-btn">
                                    <span class="btn btn-primary btn-file btn-flat">
                                        Selecionar <input name="war" type="file"  multiple >
                                    </span>
                                </span>
                                <input type="text" class="form-control" placeholder="WAR file..." readonly>
                                <span class="input-group-btn">
                                    <button name="create" class="btn btn-primary btn-file btn-flat" >
                                        <i class="fa fa-upload"></i>
                                    </button>
                                </span>
                            </div>
                        </div>

                    </g:form>
                </div>
                <div class="modal-footer">
                    %{--<button type="button" class="btn btn-default" data-dismiss="modal">Fechar</button>--}%
                </div>
            </div>

        </div>
    </div>


</div>

</body>
</html>
