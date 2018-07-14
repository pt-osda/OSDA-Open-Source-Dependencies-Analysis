package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.Report
import com.github.ptosda.projectvalidationmanager.database.entities.ReportPk
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : PagingAndSortingRepository<Report, ReportPk>