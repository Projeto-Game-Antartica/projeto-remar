<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'processGame.label', default: 'ProcessGame')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>

		<ul>
	  		<g:each var="i" in="${jogos}">
	    		<li><g:link action="iniciar_desenvolvimento" id="${i[0]}">${i[0]}</g:link></li>
	  		</g:each>
		</ul>
	</body>
</html>