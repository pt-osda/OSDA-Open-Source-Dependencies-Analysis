package com.github.ptosda.projectvalidationmanager.model.repositories

import com.github.ptosda.projectvalidationmanager.model.entities.Repo
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RepoRepository : CrudRepository<Repo, String>