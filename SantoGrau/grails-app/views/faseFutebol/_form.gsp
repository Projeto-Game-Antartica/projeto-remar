<%@ page import="br.ufscar.sead.loa.santograu.remar.FaseFutebol" %>

  <div style="margin-top:-20px; margin-bottom: 30px; color:#333333">
    Descrição e introdução do jogo.
    <center>
      <div style="margin-top:20px">
        //readme do jogo
      </div>
    </center>
  </div>

  <div style="margin-top:45px; margin-bottom: 20px; color:#333333">
    Adicione abaixo três palavras relacionadas ao conteúdo multimídia para o jogador decifrar (o jogo irá embaralhá-las automaticamente).
  </div>
  <div class="fieldcontain ${hasErrors(bean: faseTecnologiaInstance, field: 'palavras1', 'error')} required">
    <label for="palavras1">
    <g:message code="faseTecnologia.palavras1.label" default="Palavra 1: " />
    <span class="required-indicator">*</span>
  </label>
    <g:field name="palavras1" type="text" value="${faseTecnologiaInstance.palavras[0]}" required="" length="25" maxlength="25" class="validate" />
  </div>

  <div class="fieldcontain ${hasErrors(bean: faseTecnologiaInstance, field: 'palavras2', 'error')} required">
    <label for="palavras2">
    <g:message code="faseTecnologia.palavras2.label" default="Palavra 2: " />
    <span class="required-indicator">*</span>
  </label>
    <g:field name="palavras2" type="text" value="${faseTecnologiaInstance.palavras[1]}" required="" length="25" maxlength="25" class="validate" />
  </div>

  <div class="fieldcontain ${hasErrors(bean: faseTecnologiaInstance, field: 'palavras3', 'error')} required">
    <label for="palavras3">
    <g:message code="faseTecnologia.palavras3.label" default="Palavra 3: " />
    <span class="required-indicator">*</span>
  </label>
    <g:field name="palavras3" type="text" value="${faseTecnologiaInstance.palavras[2]}" required="" length="25" maxlength="25" class="validate" />
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

  <div id="errorLinkModal" class="modal">
    <div class="modal-content">
      Informe um link do Youtube válido.
    </div>
    <div class="modal-footer">
      <button class="btn waves-effect waves-light modal-close my-orange" style="margin-right: 10px;">Ok</button>
    </div>
  </div>
