package com.github.ptosda.projectvalidationmanager

import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticatedSuccessHandle : AuthenticationSuccessHandler {

    private val redirectStrategy = DefaultRedirectStrategy()

    override fun onAuthenticationSuccess(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        redirectStrategy.sendRedirect(request, response, "/");
    }

}