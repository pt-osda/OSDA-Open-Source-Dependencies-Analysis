package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.ProjectUser
import com.github.ptosda.projectvalidationmanager.database.entities.ProjectUserPk
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectUserRepository : PagingAndSortingRepository<ProjectUser, ProjectUserPk>