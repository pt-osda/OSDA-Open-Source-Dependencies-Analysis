package com.github.ptosda.projectvalidationmanager

import com.github.ptosda.projectvalidationmanager.database.entities.ProjectUser
import com.github.ptosda.projectvalidationmanager.database.entities.ProjectUserPk
import com.github.ptosda.projectvalidationmanager.database.entities.User
import com.github.ptosda.projectvalidationmanager.database.repositories.ProjectRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.ProjectUserRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService (
        val userRepository: UserRepository,
        val projectRepository: ProjectRepository,
        val projectUserRepository: ProjectUserRepository,
        val bCryptPasswordEncoder: BCryptPasswordEncoder
){
    fun save(user: User){
        user.password = bCryptPasswordEncoder.encode(user.password)
        userRepository.save(user)
    }

    fun getUser(username: String) : Optional<User> {
        return userRepository.findById(username)
    }

    fun addUserToProject(username: String, projectId: String){
        val user = userRepository.findById(username).get()
        val project = projectRepository.findById(projectId).get()
        projectUserRepository.save(ProjectUser(ProjectUserPk(project, user)))
    }

}