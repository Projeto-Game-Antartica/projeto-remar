<%@ page import="br.ufscar.sead.loa.santograu.remar.FaseFutebol" %>

<div class="fieldcontain ${hasErrors(bean: faseFutebolInstance, field: 'correctAnswer', 'error')} required">
	<label for="correctAnswer">
		<g:message code="faseFutebol.correctAnswer.label" default="Correct Answer" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="correctAnswer" type="text" value="${faseFutebolInstance.correctAnswer}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseFutebolInstance, field: 'ownerId', 'error')} required">
	<label for="ownerId">
		<g:message code="faseFutebol.ownerId.label" default="Owner Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="ownerId" type="number" value="${faseFutebolInstance.ownerId}" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: faseFutebolInstance, field: 'taskId', 'error')} required">
	<label for="taskId">
		<g:message code="faseFutebol.taskId.label" default="Task Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="taskId" required="" value="${faseFutebolInstance?.taskId}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: faseFutebolInstance, field: 'title', 'error')} required">
	<label for="title">
		<g:message code="faseFutebol.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="title" required="" value="${faseFutebolInstance?.title}"/>

</div>
