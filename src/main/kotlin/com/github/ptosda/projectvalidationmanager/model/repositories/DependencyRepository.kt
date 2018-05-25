package com.github.ptosda.projectvalidationmanager.model.repositories

import com.github.ptosda.projectvalidationmanager.model.entities.Dependency
import com.github.ptosda.projectvalidationmanager.model.entities.DependencyPk
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DependencyRepository : CrudRepository<Dependency, DependencyPk>