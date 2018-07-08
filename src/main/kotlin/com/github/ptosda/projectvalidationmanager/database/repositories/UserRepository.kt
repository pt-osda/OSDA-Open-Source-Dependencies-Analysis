package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.User
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : PagingAndSortingRepository<User, String>