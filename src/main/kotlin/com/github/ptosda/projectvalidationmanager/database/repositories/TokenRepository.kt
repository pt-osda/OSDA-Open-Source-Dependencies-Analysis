package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.Token
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : PagingAndSortingRepository<Token, String>