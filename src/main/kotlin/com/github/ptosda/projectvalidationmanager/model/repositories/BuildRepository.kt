package com.github.ptosda.projectvalidationmanager.model.repositories

import com.github.ptosda.projectvalidationmanager.model.entities.Build
import com.github.ptosda.projectvalidationmanager.model.entities.BuildPk
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BuildRepository : PagingAndSortingRepository<Build, BuildPk> {

    override fun findById(id: BuildPk): Optional<Build>

    @Query(value =  "Select name, tag, timestamp " +
            "FROM build " +
            "WHERE name = :project_name",
            nativeQuery = true)
    fun getBuildsFromProject(@Param("project_name") projectName: String) : List<Build>

}