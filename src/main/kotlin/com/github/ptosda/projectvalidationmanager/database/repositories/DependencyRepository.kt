package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.Dependency
import com.github.ptosda.projectvalidationmanager.database.entities.DependencyPk
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface DependencyRepository : PagingAndSortingRepository<Dependency, DependencyPk>