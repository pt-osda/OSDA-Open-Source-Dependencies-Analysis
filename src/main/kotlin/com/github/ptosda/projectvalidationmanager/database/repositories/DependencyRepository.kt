package com.github.ptosda.projectvalidationmanager.database.repositories

import com.github.ptosda.projectvalidationmanager.database.entities.Dependency
import com.github.ptosda.projectvalidationmanager.database.entities.DependencyPk
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DependencyRepository : PagingAndSortingRepository<Dependency, DependencyPk> {
    fun findAllByDirect(direct: Boolean, pageable: Pageable) : Page<Dependency>

    @Query(value = "SELECT id, main_version, description, direct, error_info, vulnerabilities_count, project, \"timestamp\"\n" +
            "\tFROM public.dependency\n" +
            "\tWHERE project = :project and timestamp = :timestamp and direct = 'true'",
            nativeQuery = true)
    fun getDirectDependenciesFromReport(@Param("project") project : String, @Param("timestamp") timestamp : String) : List<Dependency>
}