package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.model.repositories.BuildRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder
import java.sql.Timestamp
import java.time.Instant
import kotlin.collections.set

@Controller
@RequestMapping("/report")
class ReportController(val provider: UiProvider, val buildRepo: BuildRepository) {

    /**
     * Get the latest build reports received. Show projects and add a filter ( group, repository )
     */
    @GetMapping // TODO Think about adding version to project and description. Need to change the timestamp since it cant be used in some formats as a request variable
    fun getHome(model: HashMap<String, Any>) : String{

        model["page_title"] = "OSDA"

        val projects = provider.provideLatestProjects()


        val output = ArrayList<ProjectInfo>()

        projects.forEach{
            val buildInfos = ArrayList<BuildInfo>()
            val builds = provider.buildRepo.getAllBuildsFromProject(it.name)

            builds.forEach{
                buildInfos.add(BuildInfo(it.pk.project.name, URLEncoder.encode(it.pk.timestamp,"UTF-8"), it.tag))
            }

            output.add(ProjectInfo(it.name, output.count(), buildInfos))
        }

        model["projects"] = output

        return "index"
    }

    data class ProjectInfo(val project_name: String = "First Test ReportController",val id: Int, val builds: List<BuildInfo> = ArrayList())

    data class BuildInfo(val project_name: String = "First Test ReportController",
                         //val project_version: String = "1.0.0",
                         //val description: String = "",
                         val timestamp: String = Timestamp.from(Instant.now()).toString(),
                         val tag: String = "First Tag id",
                         val dependencies: ArrayList<Dependency> = ArrayList()
    )


    /**
     * Tem que se verificar a chave primaria de projecto pois pode haver com nomes iguais
     */
    @GetMapping("/build/{project-name}")
    fun getProjectBuilds(@RequestParam("project-name", required = false) projectName: String?,
                         model: HashMap<String, Any>) : String{

        model["page_title"] = "Builds"

        model["projects"] = provider.provideLatestProjects()

        return "index"
    }

    /**
     * Get the details of a build ( Dependencies, Licenses and Vulnerabilities )
     *
     * TODO Highlight dependencies that have vulnerabilities, by showing them in a different list than the rest for example
     *
     */
    @GetMapping("build/{project-name}/{timestamp}/detail")
    fun getBuildDetail(@RequestParam("project-name", required = false) projectName: String?,
                       @RequestParam("timestamp", required = false) timestamp: String?,
                       model: HashMap<String, Any>) : String
    {
        model["page_title"] = "BuildDetail"

        val buildInfo = provider.provideBuildDetail()

        model["project_name"] = buildInfo.project_name
        model["project_version"] = buildInfo.project_version
        model["timestamp"] = buildInfo.timestamp
        model["tag"] = buildInfo.tag
        model["dependencies"] = buildInfo.dependencies

        return "build-detail"
    }

    /**
     * Get the details of a dependency ( Dependencies, Licenses and Vulnerabilities )
     */
    @GetMapping("dependency/{id}/{version}/detail")
    fun getDependencyDetail(@RequestParam("id", required = false) dependencyId: String?,
                            @RequestParam("version", required = false) version: String?,
                            model: HashMap<String, Any>) : String
    {
        model["page_title"] = "DependencyDetail"

        return "dependency-detail"
    }

}