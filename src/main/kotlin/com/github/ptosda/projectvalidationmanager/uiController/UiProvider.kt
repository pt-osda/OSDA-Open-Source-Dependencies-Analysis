package com.github.ptosda.projectvalidationmanager.uiController

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ptosda.projectvalidationmanager.database.entities.Project
import com.github.ptosda.projectvalidationmanager.database.repositories.BuildRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.ProjectRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class UiProvider(val buildRepo: BuildRepository, val projectRepo: ProjectRepository) {

    companion object {
        val buildInfo = arrayListOf(BuildInfo(),
                BuildInfo("First Test Project",
                        timestamp= Timestamp.from(Instant.now().plusSeconds(10000000)).toString(),
                        tag="Second Tag id")
        )
        val projectInfo = arrayListOf(ProjectInfo(id= 1, builds= buildInfo),
                ProjectInfo("Second Test Project", 2,
                        arrayListOf(
                                BuildInfo("Second Test ReportController",
                                        timestamp = Timestamp.from(Instant.now()).toString(),
                                        tag="First Tag id"),
                                BuildInfo("Second Test ReportController",
                                        timestamp = Timestamp.from(Instant.now().plusSeconds(10000000)).toString(),
                                        tag="Second Tag id")))
        )

        lateinit var buildDetailInfo: BuildInfo

        val reportFileName = "report.json"

        val mapper = ObjectMapper()
    }

    /*private final fun init() {

        val reportFile = File(javaClass.classLoader.getResource(reportFileName).toURI())

        val node = mapper.readTree(reportFile)

        val formattedDependencies: ArrayList<Dependency> = ArrayList()
        node["dependencies"].asIterable().forEach {
            var licenses: String? = null
            var vulnerabilities: ArrayList<Vulnerability> = ArrayList()

            it["license"].asIterable().forEach{
                if(it["title"] != null) {
                    licenses = it["title"].asText()
                }
            }

            *//*
            it["vulnerabilityModels"].asIterable().forEach{
                val i :ArrayList<Any> = ArrayList()

                vulnerabilityModels.add(
                        VulnerabilityModel(it["vulnerability_title"].asText(),
                                it["description"].asText(),
                                StreamSupport.stream(it["references"].asIterable().spliterator(), false)
                                        .collect(Collectors.toList()) as List<String>,
                                StreamSupport.stream(it["versions"].asIterable().spliterator(), false)
                                        .collect(Collectors.toList()) as List<String>
                ))
            }*//*

            formattedDependencies.add(
                    Dependency(it["title"].asText(), it["main_version"].asText(),
                            licenses)
            )
        }

        buildDetailInfo = BuildInfo(node["name"].asText(), node["version"].asText(), node["description"].asText(), dependencies = formattedDependencies)
    }*/


    fun provideLatestBuilds(): ArrayList<BuildInfo> {
        return buildInfo
    }

    fun provideLatestProjects(): List<Project> {
        return projectRepo.findAll() as List<Project>
    }

    fun provideBuildDetail() : BuildInfo {
        return buildDetailInfo
    }
}

data class ProjectInfo(val project_name: String = "First Test ReportController",val id: Int, val builds: ArrayList<BuildInfo>)

data class BuildInfo(val project_name: String = "First Test ReportController",
                     val project_version: String = "1.0.0",
                     val description: String = "",
                     val timestamp: String = Timestamp.from(Instant.now()).toString(),
                     val tag: String = "First Tag id",
                     val dependencies: ArrayList<Dependency> = ArrayList()
)

data class Dependency(val title: String, val version: String, val license: String?)

data class License(val title: String? = "Doesn't Have a License", val origins: ArrayList<String> = ArrayList())

data class Vulnerability(val title: String, val description: String, val references: List<String>, val versions: List<String>)
