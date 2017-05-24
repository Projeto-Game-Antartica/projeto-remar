package br.ufscar.sead.loa.remar.saml

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.saml.SAMLDiscovery

/**
 * Created by hugo on 11/05/17.
 */


class newSAMLDiscovery extends SAMLDiscovery {

    protected void sendIDPSelection(HttpServletRequest request, HttpServletResponse response, String responseURL, String returnParam) throws IOException, ServletException {
        response.sendRedirect(request.getContextPath() + getIdpSelectionPath());
    }
}