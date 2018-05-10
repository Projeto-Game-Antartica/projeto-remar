<%@ page import="br.ufscar.sead.loa.santograu.remar.FaseBiblioteca" %>

<div style="margin-top:45px; margin-bottom: 20px; color:#333333">
    Adicione abaixo três palavras, cada uma com três dicas e uma resposta.
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra11', 'error')} required">
    <label for="palavra11">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 1 para palavra 1: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra11" type="text" value="${faseBibliotecaInstance?.palavra1[0]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra12', 'error')} required">
    <label for="palavra12">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 2 para palavra 1: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra12" type="text" value="${faseBibliotecaInstance?.palavra1[1]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra13', 'error')} required">
    <label for="palavra13">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 3 para palavra 1: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra13" type="text" value="${faseBibliotecaInstance?.palavra1[2]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra1', 'error')} required">
    <label for="palavra1">
        <g:message code="faseBiblioteca.palavras1.label" default="Palavra 1: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra1" type="text" value="${faseBibliotecaInstance?.palavra1[3]}" required="" length="25" maxlength="25" class="validate"/>
</div>





<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra21', 'error')} required">
    <label for="palavra21">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 1 para palavra 2: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra21" type="text" value="${faseBibliotecaInstance?.palavra2[0]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra22', 'error')} required">
    <label for="palavra22">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 2 para palavra 2: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra22" type="text" value="${faseBibliotecaInstance?.palavra2[1]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra23', 'error')} required">
    <label for="palavra23">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 3 para palavra 2: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra23" type="text" value="${faseBibliotecaInstance?.palavra2[2]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra2', 'error')} required">
    <label for="palavra2">
        <g:message code="faseBiblioteca.palavras1.label" default="Palavra 2: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra2" type="text" value="${faseBibliotecaInstance?.palavra2[3]}" required="" length="25" maxlength="25" class="validate"/>
</div>





<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra31', 'error')} required">
    <label for="palavra31">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 1 para palavra 3: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra31" type="text" value="${faseBibliotecaInstance?.palavra3[0]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra32', 'error')} required">
    <label for="palavra32">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 2 para palavra 3: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra32" type="text" value="${faseBibliotecaInstance?.palavra3[1]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra33', 'error')} required">
    <label for="palavra33">
        <g:message code="faseBiblioteca.palavras1.label" default="Dica 3 para palavra 3: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra33" type="text" value="${faseBibliotecaInstance?.palavra3[2]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: faseBibliotecaInstance, field: 'palavra3', 'error')} required">
    <label for="palavra3">
        <g:message code="faseBiblioteca.palavras1.label" default="Palavra 3: " />
        <span class="required-indicator">*</span>
    </label>
    <g:field name="palavra3" type="text" value="${faseBibliotecaInstance?.palavra3[3]}" required="" length="25" maxlength="25" class="validate"/>
</div>

<div class="buttons col s1 m1 l1 offset-s8 offset-m10 offset-l10" style="margin-top:20px">
    <button class="btn waves-effect waves-light remar-orange" type="submit" name="save" id="submitButton">
        Enviar
    </button>
</div>

<div id="errorSubmitingModal" class="modal">
    <div class="modal-content">
        Preencha todos os campos antes de finalizar a customização.
    </div>
    <div class="modal-footer">
        <button class="btn waves-effect waves-light modal-close my-orange" style="margin-right: 10px;">Ok</button>
    </div>
</div>

