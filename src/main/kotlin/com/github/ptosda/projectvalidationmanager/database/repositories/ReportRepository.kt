package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.Report
import com.github.ptosda.projectvalidationmanager.database.entities.ReportPk
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReportRepository : PagingAndSortingRepository<Report, ReportPk> {


    @Query(value = "select * from report where project = :project and timestamp = :timestamp",
            nativeQuery = true)
    fun findByProjectIdAndReportId(@Param("project") projectId: String,
                                   @Param("timestamp") timestamp: String): Optional<Report>
}