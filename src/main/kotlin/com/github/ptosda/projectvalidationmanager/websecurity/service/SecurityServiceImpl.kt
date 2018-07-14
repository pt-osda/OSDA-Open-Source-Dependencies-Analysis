package com.github.ptosda.projectvalidationmanager.websecurity.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class SecurityServiceImpl (
        val authenticationManager: AuthenticationManager,
        val userDetailsService: UserDetailsService
){
    /**
     * Retrieves the username of the currently logged in user
     * @return username
     */
    fun findLoggedInUsername() : String? {
        val userDetails = SecurityContextHolder.getContext().authentication.principal
        if(userDetails is UserDetails){
            return userDetails.username
        }

        return null
    }

    /**
     * Logs in a user that has registered
     * @param username username of the registered user
     * @param password password of the registered user
     */
    fun autoLogin(username: String, password: String) {
        val userDetails = userDetailsService.loadUserByUsername(username)
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(usernamePasswordAuthenticationToken)

        if (usernamePasswordAuthenticationToken.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        }
    }
}