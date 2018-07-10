package com.github.ptosda.projectvalidationmanager

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class SystemUser(
        val name: String,
        username: String,
        password: String,
        authorities: Collection<GrantedAuthority>
) : User(username, password, authorities)