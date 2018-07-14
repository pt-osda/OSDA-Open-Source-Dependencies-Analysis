package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.ProjectUser
import com.github.ptosda.projectvalidationmanager.database.entities.ProjectUserPk
import com.github.ptosda.projectvalidationmanager.database.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProjectUserRepository : PagingAndSortingRepository<ProjectUser, ProjectUserPk> {

    @Query(value = "select * from project_user join project on (project_id = id) where user_info_username = :username",
            nativeQuery = true)
    fun findAllByUsername(@Param("username") userName: String, pageable: Pageable): Page<ProjectUser>
}