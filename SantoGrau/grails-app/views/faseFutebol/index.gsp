<%@ page import="br.ufscar.sead.loa.santograu.remar.FaseFutebol" %>
<!DOCTYPE html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Em Busca do Santo Grau</title>
		<g:external dir="css" file="faseFutebol.css" />
		<script type="text/javascript" src="/santograu/js/faseFutebol.js"></script>
	</head>

	<body>
		<div class="cluster-header">
			<p class="text-teal text-darken-3 left-align margin-bottom" style="font-size: 28px;">Fase Campo de Futebol - Banco de Questões</p>
		</div>
		<div class="row">
			<div style="margin-bottom:10px;color:#333333">
				Coloque aqui descrição do jogo + tutorial.
				<div style="margin-top:20px;margin-bottom:15px;text-align:center">Colocar imagem do readme</div>
				Selecione 2 desafios.
			</div>
			<div id="chooseQuestion" class="col s12 m12 l12">
				<br>
				<div class="row">
					<div class="col s6 m3 l3 offset-s6 offset-m9 offset-l9">
						<input type="text" id="SearchLabel" placeholder="Buscar" />
					</div>
				</div>
				<div class="row">
					<div class="col s12 m12 l12">
						<table class="highlight" id="table" style="margin-top: -30px;">
							<thead>
								<tr>
									<th>Selecionar
										<div class="row" style="margin-bottom: -10px;">
											<button style="margin-left: 3px; background-color: #795548" class="btn-floating " id="BtnCheckAll" onclick="check_all()"><i  class="material-icons">check_box_outline_blank</i></button>
											<button style="margin-left: 3px; background-color: #795548" class="btn-floating " id="BtnUnCheckAll" onclick="uncheck_all()"><i  class="material-icons">done</i></button>
										</div>
									</th>
									<th id="titleLabel">Pergunta
										<div class="row" style="margin-bottom: -10px;"><button class="btn-floating" style="visibility: hidden"></button></div>
									</th>
									<th>Resposta
										<div class="row" style="margin-bottom: -10px;"><button class="btn-floating" style="visibility: hidden"></button></div>
									</th>
									<th>Ações
										<div class="row" style="margin-bottom: -10px;"><button class="btn-floating" style="visibility: hidden"></button></div>
									</th>
								</tr>
							</thead>

							<tbody>

							</tbody>
						</table>
					</div>
				</div>

				<div class="row">
					<div class="col s1 m1 l1 offset-s4 offset-m8 offset-l8">
						<a data-target="createModal" name="create" class="btn-floating btn-large waves-effect waves-light modal-trigger my-orange tooltipped" data-tooltip="Criar questão"><i class="material-icons">add</i></a>
					</div>
					<div class="col s1 offset-s1 m1 l1">
						<a name="delete" class="btn-floating btn-large waves-effect waves-light my-orange tooltipped" data-tooltip="Exluir questão"><i class="material-icons" onclick="_open_modal_delete()">delete</i></a>
					</div>
					<div class="col s1 offset-s1 m1 l1">
						<a data-target="uploadModal" class="btn-floating btn-large waves-effect waves-light my-orange modal-trigger tooltipped" data-tooltip="Upload arquivo .csv"><i
							class="material-icons">file_upload</i></a>
					</div>
					<div class="col s1 offset-s1 m1 l1">
						<a class="btn-floating btn-large waves-effect waves-light my-orange tooltipped" data-tooltip="Exportar questões para .csv"><i
							class="material-icons" onclick="exportQuestions()">file_download</i></a>
					</div>
				</div>

				<div class="row">
					<div class="col s2">
						<button class="btn waves-effect waves-light remar-orange" name="save" id="submitButton" onclick="_submit()">Enviar</button>
					</div>
				</div>

				<div id="editModal" class="modal">
					<div class="modal-content">
						<h4>Editar Questão</h4>
						<div class="row">
							<g:form method="post" action="update" resource="${faseFutebolInstance}">
								<div class="row">
									<div class="input-field col s12">
										<label id="labelTitle" class="active" for="editTitle">Pergunta</label>
										<input id="editTitle" name="title" required="" type="text" class="validate" length="95" maxlength="95">
									</div>
								</div>

								<div class="row">
									<div class="input-field col s9">
										<label id="correctAnswerLabel" class="active" for="correctAnswerID">Resposta</label>
										<input type="text" class="form-control" id="correctAnswerID" name="correctAnswer" required="" maxlength="40" length="20" />
									</div>
								</div>
								<input type="hidden" id="faseFutebolMinadoID" name="faseFutebolID">
								<div class="col l10">
									<g:submitButton name="update" class="btn btn-success btn-lg my-orange" value="Salvar" />
								</div>
							</g:form>
						</div>
					</div>
				</div>

				<div id="createModal" class="modal">
					<div class="modal-content">
						<h4>Criar Questão</h4>
						<div class="row">
							<g:form action="save" resource="${faseFutebolInstance}">
								<div class="row">
									<div class="input-field col s12">
										<label id="labelTitleCreate" class="active" for="editTitleCreate">Pergunta</label>
										<input id="editTitleCreate" name="title" required="" type="text" class="validate" length="95" maxlength="95">
									</div>
								</div>

								<div class="row">
									<div class="row">
										<div class="input-field col s9">
											<label id="labelAnswerCreate" class="active" for="editAnswerCreate">Resposta</label>
											<input type="text" class="form-control" id="editAnswerCreate" name="correctAnswer" required="" maxlength="40" length="15" />
										</div>
									</div>
									<div class="col l10">
										<g:submitButton name="create" class="btn btn-success btn-lg my-orange" value="Criar" />
									</div>
							</g:form>
							</div>
						</div>
					</div>

					<div id="deleteModal" class="modal">
						<div class="modal-content">
							<div id="delete-one-question">
								Você tem certeza que deseja excluir essa questão?
							</div>
							<div id="delete-several-questions">
								Você tem certeza que deseja excluir essas questões?
							</div>
						</div>
						<div class="modal-footer">
							<button class="btn waves-effect waves-light modal-close my-orange" onclick="_delete()" style="margin-right: 10px;">Sim</button>
							<button class="btn waves-effect waves-light modal-close my-orange" style="margin-right: 10px;">Não</button>
						</div>
					</div>

					<div id="erroDeleteModal" class="modal">
						<div class="modal-content">
							Você deve selecionar ao menos uma questão para excluir.
						</div>
						<div class="modal-footer">
							<button class="btn waves-effect waves-light modal-close my-orange" style="margin-right: 10px;">Ok</button>
						</div>
					</div>

					<div id="errorSaveModal" class="modal">
						<div class="modal-content">
							Você deve selecionar 2 desafios.
						</div>
						<div class="modal-footer">
							<button class="btn waves-effect waves-light modal-close my-orange" style="margin-right: 10px;">Ok</button>
						</div>
					</div>


					<div id="errorDownloadModal" class="modal">
						<div class="modal-content">
							Você deve selecionar ao menos uma questão antes de exportar seu banco de questões.
						</div>
						<div class="modal-footer">
							<button class="btn waves-effect waves-light modal-close my-orange" style="margin-right: 10px;">Ok</button>
						</div>
					</div>
					<div id="errorImportingQuestionsModal" class="modal">
						<div class="modal-content">
							Erro - Para importar questões, você deve deixá-las no formado indicado.
						</div>
						<div class="modal-footer">
							<button class="btn waves-effect waves-light modal-close my-orange" style="margin-right: 10px;">Ok</button>
						</div>
					</div>

					<div id="uploadModal" class="modal">
						<div class="modal-content">
							<h4>Enviar arquivo .csv</h4>
							<div class="row">
								<g:uploadForm action="generateQuestions">
									<div class="file-field input-field">
										<div class="btn my-orange">
											<span>File</span>
											<input type="file" accept="text/csv" id="csv" name="csv">
										</div>
										<div class="file-path-wrapper">
											<input class="file-path validate" type="text">
										</div>
									</div>
									<div class="row">
										<div class="col s1 offset-s10">
											<g:submitButton class="btn my-orange" name="csv" value="Enviar" />
										</div>
									</div>
								</g:uploadForm>
							</div>

							<blockquote>Formatação do arquivo .csv</blockquote>
							<div class="row">
								<div class="col s12">
									<ol>
										<li>O separador do arquivo .csv deve ser <b> ';' (ponto e vírgula)</b> </li>
										<li>O arquivo deve ser composto apenas por <b>dados</b></li>
										<li>O arquivo deve representar a estrutura da tabela de exemplo</li>
									</ol>
									<ul>
										<li><a href="/santograu/samples/exemploSantoGrau.csv">Download do arquivo exemplo</a></li>
									</ul>
								</div>
							</div>
							<div class="row">
								<div class="col s12">
									<table class="center" style="font-size: 12px;">
										<thead>
											<tr>
												<th>Pergunta</th>
												<th>Resposta</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td>Pergunta 1</td>
												<td>Resposta 1</td>
												<td>1</td>
											</tr>
											<tr>
												<td>Pergunta 2</td>
												<td>Resposta 2</td>
												<td>3</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<input type="hidden" id="errorImportingQuestions" name="errorImportingQuestions" value="${errorImportQuestions}">
	</body>

</html>
