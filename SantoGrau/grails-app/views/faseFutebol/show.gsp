<%@ page import="br.ufscar.sead.loa.santograu.remar.FaseFutebol" %>
	<!DOCTYPE html>
	<html>

	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'faseFutebol.label', default: 'FaseFutebol')}" />
		<title>
			<g:message code="default.show.label" args="[entityName]" />
		</title>
	</head>

	<body>
		<a href="#show-faseFutebol" class="skip" tabindex="-1">
			<g:message code="default.link.skip.label" default="Skip to content&hellip;" />
		</a>
		<div class="nav" role="navigation">
			<ul>
				<li>
					<a class="home" href="${createLink(uri: '/')}">
						<g:message code="default.home.label" />
					</a>
				</li>
				<li>
					<g:link class="list" action="index">
						<g:message code="default.list.label" args="[entityName]" /></g:link>
				</li>
				<li>
					<g:link class="create" action="create">
						<g:message code="default.new.label" args="[entityName]" /></g:link>
				</li>
			</ul>
		</div>
		<div id="show-faseFutebol" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list faseFutebol">
				<g:if test="${faseFutebolInstance?.title}">
					<li class="fieldcontain">
						<span id="answers-label" class="property-label"><g:message code="faseFutebol.title.label" default="Pergunta" /></span>
					</li>
				</g:if>
				<g:if test="${faseFutebolInstance?.correctAnswer}">
					<li class="fieldcontain">
						<span id="correctAnswer-label" class="property-label"><g:message code="faseFutebol.correctAnswer.label" default="Resposta" /></span>
						<span class="property-value" aria-labelledby="correctAnswer-label"><g:fieldValue bean="${faseFutebolInstance}" field="correctAnswer"/></span>

					</li>
				</g:if>
			</ol>
			<g:form url="[resource:faseFutebolInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${faseFutebolInstance}">
						<g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>

	</html>
