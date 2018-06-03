package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.Report
import com.github.ptosda.projectvalidationmanager.database.entities.ReportPk
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : PagingAndSortingRepository<Report, ReportPk> {
    @Query(value =  "Select name, tag, timestamp " +
            "FROM report " +
            "WHERE name = :project_name",
            nativeQuery = true)
    fun getBuildsFromProject(@Param("project_name") projectName: String) : List<Report>
}