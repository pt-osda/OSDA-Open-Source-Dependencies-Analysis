package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.database.entities.Project
import com.github.ptosda.projectvalidationmanager.database.repositories.ProjectRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class ReportFilterController(val reportFilterService: ReportFilterService,
                             val projectRepo: ProjectRepository) {

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
            "detail" to { projectId, buildId -> reportFilterService.getBuildDetailView(projectId, buildId) },
            "licenses" to { projectId, buildId -> reportFilterService.getBuildLicensesView(projectId, buildId) },
            "vulnerabilities" to { projectId, buildId -> reportFilterService.getBuildVulnerabilitiesView(projectId, buildId) }
    )

    /**
     * Gets the view for either of three elements of a Project. These are its detail, licenses or vulnerabilities
     * @param projectId the if of the project to filter
     * @param filterType the type of filtering to be done (detail, licenses or vulnerabilities)
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
     * Gets the view for either of three elements of a Report. These are its detail, licenses or vulnerabilities
     * @param projectId the if of the project that the report belongs
     * @param buildId the if of the report to filter
     * @param filterType the type of filtering to be done (detail, licenses or vulnerabilities)
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

}