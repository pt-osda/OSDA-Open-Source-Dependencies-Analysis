package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.BuildRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.DependencyRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.DependencyVulnerabilityRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.VulnerabilityRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.sql.Timestamp
import java.time.Instant
import kotlin.collections.set

@Controller
@RequestMapping("/report")
class ReportController(val provider: UiProvider, val buildRepo: BuildRepository, val dependencyVulnerabilityRepo: DependencyVulnerabilityRepository, val dependencyRepo: DependencyRepository, val vulnerabilityRepo: VulnerabilityRepository) {

    /**
     * Get the latest build reports received. Show projects and add a filter ( group, repository )
     */
    @GetMapping
    fun getHome(model: HashMap<String, Any>) : String{

        model["page_title"] = "Home"

        val projects = provider.provideLatestProjects()

        val output = ArrayList<ProjectInfo>()

        projects.forEach{
            val buildInfos = ArrayList<BuildInfo>()
            val builds = buildRepo.getBuildsFromProject(it.name)

            builds.forEach{
                buildInfos.add(BuildInfo(it.pk.project.name, it.pk.timestamp, it.tag!!))
            }

            output.add(ProjectInfo(it.name, output.count(), buildInfos))
        }

        model["projects"] = output

        return "index"
    }

    data class ProjectInfo(val project_name: String = "First Test ReportController", val id: Int, val builds: List<BuildInfo> = ArrayList())

    data class BuildInfo(val project_name: String = "First Test ReportController",
                         val timestamp: String = Timestamp.from(Instant.now()).toString(),
                         val tag: String = "First Tag id",
                         val dependencies: ArrayList<Dependency> = ArrayList()
    )


    /**
     * Tem que se verificar a chave primaria de projecto pois pode haver com nomes iguais
     */
    @GetMapping("project/{project-name}/build")
    fun getProjectBuilds(@PathVariable("project-name") projectName: String?,
                         model: HashMap<String, Any>) : String{

        model["page_title"] = "Project Builds"

        model["projects"] = provider.provideLatestProjects()

        return "index"
    }

    /**
     * Get the details of a build ( Dependencies, Licenses and Vulnerabilities )
     *
     * TODO Highlight dependencies that have vulnerabilities, by showing them in a different list than the rest for example
     *
     */
    @GetMapping("project/{project-name}/build/{timestamp}/detail")
    fun getBuildDetail(@PathVariable("project-name") projectName: String,
                       @PathVariable("timestamp") timestamp: String,
                       model: HashMap<String, Any>) : String
    {
        model["page_title"] = "Build Detail"

        val buildInfo = buildRepo.findById(BuildPk(timestamp, Project(projectName, null, null)))

        if(!buildInfo.isPresent) {
            throw Exception("Build not found")
        }

        val build = buildInfo.get()

        model["project_name"] = projectName
        model["timestamp"] = build.pk.timestamp
        model["tag"] = build.tag!!
        model["dependencies"] = build.dependency!!

        return "build-detail"
    }

    /**
     * Get the details of a dependency ( Dependencies, Licenses and Vulnerabilities )
     */
    @GetMapping("project/{project-id}/build/{build-id}/dependency/{id}/{version}/detail")
    fun getDependencyDetail(@PathVariable("project-id") projectId: String,
                            @PathVariable("build-id") buildId: String,
                            @PathVariable("id") dependencyId: String,
                            @PathVariable("version") version: String,
                            model: HashMap<String, Any>) : String
    {
        model["page_title"] = "Dependency Detail"

        val dependencyInfo = dependencyRepo.findById(DependencyPk(dependencyId, Build(BuildPk(buildId, Project(projectId, null, null)), null, null), version))

        //val vulnerability = vulnerabilityRepo.findById(12345L) THROWS SerializationException

        if(!dependencyInfo.isPresent) {
            throw Exception("Dependency not found")
        }

        val dependency = dependencyInfo.get()

        model["title"] = dependency.pk.id
        model["main_version"] = dependency.pk.mainVersion
        model["description"] = dependency.description
        model["license"] = dependency.license

        return "dependency-detail"
    }

}