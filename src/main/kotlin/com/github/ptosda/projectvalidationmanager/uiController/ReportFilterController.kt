package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.database.repositories.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import com.github.ptosda.projectvalidationmanager.database.entities.Dependency

@Controller
@RequestMapping("/report")
class ReportFilterController(val projectRepo: ProjectRepository,
                             val buildRepo: BuildRepository,
                             val dependencyVulnerabilityRepo: DependencyVulnerabilityRepository,
                             val dependencyRepo: DependencyRepository,
                             val vulnerabilityRepo: VulnerabilityRepository,
                             val organizationRepository: OrganizationRepository,
                             val repoRepository: RepoRepository,
                             val depVul: DependencyVulnerabilityRepository) {

    /*val filterFunctions = hashMapOf<String, (String) -> ArrayList<Any>>(
            "None" to {_ -> getNoneFilter()},
            "Project" to {projectId -> getProjectFilter(projectId)},
            "Organization" to {organizationId -> getGroupFilter(organizationId)},
            "Repo" to {repositoryId -> getRepoFilter(repositoryId)}

    )

    @GetMapping("/filter/{type}/{text}")
    fun filter(@PathVariable("type") filterType: String,
               @PathVariable("text") filterText: String,
               model: HashMap<String, Any>) : String
    {

        model["projects"] = filterFunctions[filterType]!!.invoke(filterText)

        return "project-list-filter"
    }

    data class ProjectInfo(val project_name: String = "First Test ReportController", val id: Int, val builds: List<BuildInfo> = ArrayList())

    data class BuildInfo(val project_name: String,
                         val timestamp: String,
                         val tag: String,
                         val dependencies: ArrayList<Dependency>
    )

    fun getNoneFilter() : ArrayList<Any> {
        val projects = projectRepo.findAll()

        val validProjects = ArrayList<Any>()

        projects.forEach{
            val buildInfos = ArrayList<ReportController.BuildInfo>()
            val builds = buildRepo.getBuildsFromProject(it.name)

            builds.forEach{
                buildInfos.add(ReportController.BuildInfo(it.pk.project.name, it.pk.timestamp, it.tag!!))
            }

            validProjects.add(ReportController.ProjectInfo(it.name, validProjects.count(), buildInfos))
        }

        return validProjects
    }

    fun getProjectFilter(projectId: String) : ArrayList<Any>{
        val projects = projectRepo.findAll()

        val validProjects = ArrayList<Any>()

        projects.forEach{

            if(it.name.contains(projectId, true)) {
                val buildInfos = ArrayList<ReportController.BuildInfo>()
                val builds = buildRepo.getBuildsFromProject(it.name)

                builds.forEach{
                    buildInfos.add(ReportController.BuildInfo(it.pk.project.name, it.pk.timestamp, it.tag))
                }

                validProjects.add(ReportController.ProjectInfo(it.name, validProjects.count(), buildInfos))
            }
        }

        return validProjects
    }

    fun getGroupFilter(organizationId: String) : ArrayList<Any> {

        val organizations = organizationRepository.findAll()

        val validProjects = ArrayList<Any>()

        organizations.forEach{
            if(it.name.contains(organizationId, true)) {
                it.repo.forEach {
                    it.project.forEach {
                        val buildInfos = ArrayList<ReportController.BuildInfo>()
                        val builds = buildRepo.getBuildsFromProject(it.name)

                        builds.forEach {
                            buildInfos.add(ReportController.BuildInfo(it.pk.project.name, it.pk.timestamp, it.tag!!))
                        }

                        validProjects.add(ReportController.ProjectInfo(it.name, validProjects.count(), buildInfos))
                    }
                }
            }
        }

        return validProjects
    }

    fun getRepoFilter(repoId: String) : ArrayList<Any> {

        val repositories = repoRepository.findAll()

        val validProjects = ArrayList<Any>()

        repositories.forEach {
            if(it.name.contains(repoId, true)) {
                it.project.forEach {
                    val buildInfos = ArrayList<ReportController.BuildInfo>()
                    val builds = buildRepo.getBuildsFromProject(it.name)

                    builds.forEach {
                        buildInfos.add(ReportController.BuildInfo(it.pk.project.name, it.pk.timestamp, it.tag!!))
                    }

                    validProjects.add(ReportController.ProjectInfo(it.name, validProjects.count(), buildInfos))
                }
            }
        }

        return validProjects
    }*/

}