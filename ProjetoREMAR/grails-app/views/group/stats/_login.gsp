
    <style>
    body {
        background-color: #F2F2F2;
    }
    </style>

<div class="container">
    <div class="row">
        <div class="card white col s12 m6 l5 offset-m3 offset-l4 offset-vertical-2" style="margin-top: 30px;">
            <div class="card-content" style="padding: 20px !important;">
                <div class="card-image" style="padding-bottom: 20px;">
                    <img src="./logo-remar-preto-transparente.png">
                </div> <!-- card-image -->
                %{--<form>--}%
                    <g:if test="${flash.message}">
                        <div class="input-field" id="input-login-error">
                            <i class="material-icons small red-text">error</i><span class="align-with-icon-small red-text">
                            Usuário ou senha inválidos</span>
                            <div class="divider"></div>
                        </div>
                    </g:if>
                    <div class="row">
                        <div class="row no-margin-bottom">
                            <div class="input-field col s12">
                                <i class="material-icons prefix">account_circle</i>
                                <input id="username" name="username" type="text">
                                <label for="username">Usuário</label>
                            </div> <!-- input-field -->
                        </div>
                        <div class="row no-margin-bottom">
                            <div class="input-field col s12">
                                <i class="material-icons prefix">lock</i>
                                <input id="password" name="password" type="password">
                                <label for="password">Senha</label>
                            </div> <!-- input-field -->
                        </div>
                        <div class="row no-margin-bottom">
                            <div class="input-field center col s12">
                                <button type="submit" data-choice="login" class="btn waves-effect waves-light my-orange">Entrar</button>
                            </div>
                            <div class="input-field center col s12">
                                <button type="submit" data-choice="offline" class="btn waves-effect waves-light my-orange">Jogar Offline</button>
                            </div>
                        </div>
                        <div class="row no-margin-bottom">
                            <div class="input-field col s6 m6 l6">
                                <g:link class="margin" mapping="resetPassword">Esqueceu sua senha?</g:link> <br>
                                <g:link mapping="recoverAccount"> Reativar minha conta </g:link>
                            </div>
                            <div class="input-field col s6 m6 l6 right-align">
                                <g:link mapping="signup">Cadastre-se</g:link>
                            </div>
                        </div> <!-- input field -->

                    </div> <!-- row -->
                %{--</form>--}%
            </div> <!-- card-content -->
        </div> <!-- card -->
    </div> <!-- row -->
</div> <!-- container -->
    <script type="text/javascript" src="js/stats.js"></script>
    <script>
$('body').on('click','button', function(){
    submitLogin(this);
});
</script>