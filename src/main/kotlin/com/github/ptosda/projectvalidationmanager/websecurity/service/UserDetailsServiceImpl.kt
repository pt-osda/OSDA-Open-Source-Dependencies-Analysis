package com.github.ptosda.projectvalidationmanager.websecurity.service

import com.github.ptosda.projectvalidationmanager.database.repositories.UserRepository
import com.github.ptosda.projectvalidationmanager.websecurity.model.SystemUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service("userDetailsService")
class UserDetailsServiceImpl(
        val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findById(username)
        if(!user.isPresent){
            throw UsernameNotFoundException("User not found")
        }
        val userInfo = user.get()
        return SystemUser(userInfo.name, userInfo.username, userInfo.password, authorities())
    }

    private fun authorities() = listOf(SimpleGrantedAuthority("USER"))
}