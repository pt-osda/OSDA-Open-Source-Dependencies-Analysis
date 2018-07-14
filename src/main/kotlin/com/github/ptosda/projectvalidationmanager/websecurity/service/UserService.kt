package com.github.ptosda.projectvalidationmanager.websecurity.service

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
    /**
     * Registers a user in the database, with its password encoded
     * @param user the user to be registered
     */
    fun save(user: User){
        user.password = bCryptPasswordEncoder.encode(user.password)
        userRepository.save(user)
    }

    /**
     * Retrieves the username specified, if exists.
     * @param username the username of the user to be retrieved
     * @return optional value that contains a user only if exists a user with that username
     */
    fun getUser(username: String) : Optional<User> {
        return userRepository.findById(username)
    }

    /**
     * Authorizes a user to see a project
     * @param username the username of the user that will be authorized
     * @param projectId the identifier of the project, of which the user will have authorization to see
     */
    fun addUserToProject(username: String, projectId: String){
        val user = userRepository.findById(username).get()
        val project = projectRepository.findById(projectId).get()
        projectUserRepository.save(ProjectUser(ProjectUserPk(project, user)))
    }
}