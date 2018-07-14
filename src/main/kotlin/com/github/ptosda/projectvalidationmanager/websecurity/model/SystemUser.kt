package com.github.ptosda.projectvalidationmanager.websecurity.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

/**
 * Class that represents a user of the application
 */
class SystemUser (
        val name: String,
        username: String,
        password: String,
        authorities: Collection<GrantedAuthority>
) : User(username, password, authorities)