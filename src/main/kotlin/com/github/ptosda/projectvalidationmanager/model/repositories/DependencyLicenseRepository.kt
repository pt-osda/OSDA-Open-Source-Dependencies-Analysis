package com.github.ptosda.projectvalidationmanager.model.repositories

import com.github.ptosda.projectvalidationmanager.model.entities.DependencyLicense
import com.github.ptosda.projectvalidationmanager.model.entities.DependencyLicensePk
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface DependencyLicenseRepository : PagingAndSortingRepository<DependencyLicense, DependencyLicensePk>