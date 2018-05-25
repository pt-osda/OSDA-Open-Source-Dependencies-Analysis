package com.github.ptosda.projectvalidationmanager.model.repositories

import com.github.ptosda.projectvalidationmanager.model.entities.Build
import com.github.ptosda.projectvalidationmanager.model.entities.BuildPk
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BuildRepository : CrudRepository<Build, BuildPk> {

    @Query(value =  "Select name,tag,timestamp " +
                    "FROM build " +
                    "WHERE name = :project_name ",
            nativeQuery = true)
    fun getAllBuildsFromProject(@Param("project_name") projectName: String) : List<Build>
}