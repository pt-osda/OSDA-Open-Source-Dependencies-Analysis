package com.github.ptosda.projectvalidationmanager.webapp.controller

import com.github.ptosda.projectvalidationmanager.database.entities.Project
import com.github.ptosda.projectvalidationmanager.database.repositories.DependencyRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.LicenseRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.ProjectRepository
import com.github.ptosda.projectvalidationmanager.webapp.service.ReportFilterService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/")
class ReportFilterController(val reportFilterService: ReportFilterService,
                             val projectRepo: ProjectRepository,
                             val dependencyRepo: DependencyRepository,
                             val licenseRepo: LicenseRepository)
{

    /**
     * HashMap with all project filter types and corresponding functions
     */
    val projectFilterFunctions = hashMapOf<String, (Project) -> HashMap<String, Any?>>(
            "detail" to { project -> reportFilterService.getProjectDetailView(project) },
            "licenses" to { project -> reportFilterService.getProjectLicensesView(project) },
            "vulnerabilities" to { project -> reportFilterService.getProjectVulnerabilitiesView(project) }
    )

    /**
     * HashMap with all report filter types and corresponding functions
     */
    val buildFilterFunctions = hashMapOf<String, (String, String) -> HashMap<String, Any?>>(
            "detail" to { projectId, buildId -> reportFilterService.getReportDetailView(projectId, buildId) },
            "licenses" to { projectId, buildId -> reportFilterService.getReportLicensesView(projectId, buildId) },
            "vulnerabilities" to { projectId, buildId -> reportFilterService.getReportVulnerabilitiesView(projectId, buildId) }
    )

    /**
     * HashMap with all report search functions
     */
    val searchFilterFunctions = hashMapOf<String, (String) -> HashMap<String, Any?>>(
            "projs" to { searchText ->
                hashMapOf( "projects" to projectRepo.findAll().filter { it.name.startsWith(searchText, true) },
                        "view_name" to "projects"
                )
            },
            "dependencies" to { searchText ->
                hashMapOf("dependencies" to dependencyRepo.findAll()
                        .groupBy { it.pk.id + it.pk.mainVersion }
                        .values
                        .map { it.last() }
                        .sortedBy{ it.pk.id }
                        .filter { it.pk.id.startsWith(searchText, true) },
                        "view_name" to "dependencies"
                )
            },
            "licenses" to { searchText ->
                hashMapOf( "licenses" to licenseRepo.findAll().filter { it.spdxId.trimStart('(').startsWith(searchText, true) },
                        "view_name" to "generic-licenses"
                )
            }
    )

    /**
     * Gets the view for either of three elements of a Project. These are its detail, licenses or vulnerabilities
     * @param projectId the if of the project to filter
     * @param filterType the type of filtering to be done (detail, licenses or vulnerabilities)
     * @param model hash map to store all view related information
     */
    @GetMapping("projs/{project-id}/filter/{filter-type}")
    fun filterProject(@PathVariable("project-id") projectId: String,
                      @PathVariable("filter-type") filterType: String,
                      model: HashMap<String, Any?>) : String
    {

        val projectInfo = projectRepo.findById(projectId)

        if(!projectInfo.isPresent) {
            throw Exception("Project doesn't exist")
        }

        val project = projectInfo.get()


        model.putAll(projectFilterFunctions[filterType]!!.invoke(project))

        return model["view_name"].toString()
    }

    /**
     * Gets the view for any of the three possible elements of a Report. These are its detail, licenses or vulnerabilities
     * @param projectId the if of the project that the report belongs
     * @param buildId the if of the report to filter
     * @param filterType the type of filtering to be done (detail, licenses or vulnerabilities)
     * @param model hash map to store all view related information
     */
    @GetMapping("projs/{project-id}/report/{report-id}/filter/{filter-type}")
    fun filterBuild(@PathVariable("project-id") projectId: String,
                    @PathVariable("report-id") buildId: String,
                    @PathVariable("filter-type") filterType: String,
                    model: HashMap<String, Any?>) : String
    {

        model.putAll(buildFilterFunctions[filterType]!!.invoke(projectId, buildId))

        return model["view_name"].toString()
    }

    /**
     * Gets the view for a search request
     * @param searchType the type of search to be done
     * @param searchValue the value used in the search
     * @param model hash map to store all view related information
     */
    @GetMapping("search/{search-type}")
    fun search(@PathVariable("search-type") searchType: String,
               @RequestParam("value") searchValue: String,
               model: HashMap<String, Any?>) : String
    {

        model.putAll(searchFilterFunctions[searchType]!!.invoke(searchValue))

        return model["view_name"].toString()
    }
}