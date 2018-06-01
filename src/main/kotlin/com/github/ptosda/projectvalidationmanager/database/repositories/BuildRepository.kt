package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.Build
import com.github.ptosda.projectvalidationmanager.database.entities.BuildPk
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BuildRepository : PagingAndSortingRepository<Build, BuildPk> {
    @Query(value =  "Select name, tag, timestamp " +
            "FROM build " +
            "WHERE name = :project_name",
            nativeQuery = true)
    fun getBuildsFromProject(@Param("project_name") projectName: String) : List<Build>
}