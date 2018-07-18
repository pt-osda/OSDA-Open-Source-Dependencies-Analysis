package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.Dependency
import com.github.ptosda.projectvalidationmanager.database.entities.DependencyPk
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.io.Serializable

@Repository
interface DependencyRepository : PagingAndSortingRepository<Dependency, DependencyPk> {
    @Query(value = "SELECT d.id AS id, d.main_version AS mainVersion, d.vulnerabilities_count AS vulnerabilitiesCount, d.direct AS direct, REPLACE(d.id, '/', ':') AS pathId " +
            "FROM dependency d " +
            "WHERE d.direct='true' " +
            "GROUP BY d.id, d.main_version, d.vulnerabilities_count, d.direct " +
            "ORDER BY d.id, d.main_version",
            nativeQuery = true)
    fun findDistinctDirectDependencies(pageable: Pageable) : Page<DistinctDependency>

    @Query(value = "SELECT id, main_version, private_versions, description, direct, vulnerabilities_count, project, \"timestamp\"\n" +
            "\tFROM public.dependency\n" +
            "\tWHERE project = :project and timestamp = :timestamp and direct = 'true'",
            nativeQuery = true)
    fun getDirectDependenciesFromReport(@Param("project") project : String, @Param("timestamp") timestamp : String) : List<Dependency>
}

interface DistinctDependency {
    fun getId() : String

    fun getMainVersion() : String

    fun getVulnerabilitiesCount() : Int

    fun getDirect() : Boolean

    fun getPathId() : String
}