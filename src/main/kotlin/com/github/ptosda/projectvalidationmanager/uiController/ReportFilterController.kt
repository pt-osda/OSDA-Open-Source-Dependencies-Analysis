package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.model.repositories.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/report")
class ReportFilterController(val projectRepo: ProjectRepository, val buildRepo: BuildRepository, val dependencyVulnerabilityRepo: DependencyVulnerabilityRepository, val dependencyRepo: DependencyRepository, val vulnerabilityRepo: VulnerabilityRepository) {

    val filterFunctions = hashMapOf<String, (String) -> ArrayList<Any>>(
            "Project" to {projectId -> filterProject(projectId)}
    )

    @GetMapping("/filter/{type}/{text}")
    fun filter(@PathVariable("type") filterType: String,
               @PathVariable("text") filterText: String) : ArrayList<Any>
    {
       val t = ""
       return filterFunctions[filterType]!!.invoke(filterText)
    }

    fun filterProject(projectId: String) : ArrayList<Any>{
        val projects = projectRepo.findAll()

        val validProjects = ArrayList<Any>()

        projects.forEach{
            if(it.name.contains(projectId))
                validProjects.add(it)
        }

        return validProjects
    }
}