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

    val projectFilterFunctions = hashMapOf<String, (Project) -> HashMap<String, Any?>>(
            "detail" to { project -> reportFilterService.getProjectDetailView(project) },
            "licenses" to { project -> reportFilterService.getProjectLicensesView(project) },
            "vulnerabilities" to { project -> reportFilterService.getProjectVulnerabilitiesView(project) }
    )

    val buildFilterFunctions = hashMapOf<String, (String, String) -> HashMap<String, Any?>>(
            "detail" to { projectId, buildId -> reportFilterService.getBuildDetailView(projectId, buildId) },
            "licenses" to { projectId, buildId -> reportFilterService.getBuildLicensesView(projectId, buildId) },
            "vulnerabilities" to { projectId, buildId -> reportFilterService.getBuildVulnerabilitiesView(projectId, buildId) }
    )

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

    @GetMapping("projs/{project-id}/report/{build-id}/filter/{filter-type}")
    fun filterBuild(@PathVariable("project-id") projectId: String,
               @PathVariable("build-id") buildId: String,
               @PathVariable("filter-type") filterType: String,
               model: HashMap<String, Any?>) : String
    {

        model.putAll(buildFilterFunctions[filterType]!!.invoke(projectId, buildId))

        return model["view_name"].toString()
    }

}