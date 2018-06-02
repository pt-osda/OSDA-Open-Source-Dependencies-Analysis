package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.database.entities.BuildPk
import com.github.ptosda.projectvalidationmanager.database.entities.DependencyLicense
import com.github.ptosda.projectvalidationmanager.database.entities.DependencyVulnerability
import com.github.ptosda.projectvalidationmanager.database.entities.Project
import com.github.ptosda.projectvalidationmanager.database.repositories.BuildRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.ProjectRepository
import org.springframework.stereotype.Service

@Service
class ReportFilterService(private val reportService: ReportService,
                          private val buildRepo: BuildRepository) {

    fun getProjectDetailView(project: Project) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        model["project_id"] = project.name
        model["builds"] = project.build

        model["view_name"] = "project-detail"

        return model
    }

    fun getBuildDetailView(projectId: String, buildId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val buildInfo = buildRepo.findById(BuildPk(buildId, Project(projectId, null, null)))

        if(!buildInfo.isPresent) {
            throw Exception("Build was not found")
        }

        val build = buildInfo.get()

        model["project_id"] = projectId

        model["build_id"] = buildId
        model["build_tag"] = build.tag

        model.putAll(reportService.getBuildDependencies(build))

        model["view_name"] = "build-detail"

        return model
    }

    fun getProjectLicensesView(project: Project) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()
        val licenses = ArrayList<DependencyLicense>()

        project.build?.forEach {
            it.dependency!!.forEach {
                licenses.addAll(it.license)
            }
        }

        model["licenses"] = licenses
        model["view_name"] = "licenses"

        return model
    }

    fun getBuildLicensesView(projectId: String, buildId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val buildInfo = buildRepo.findById(BuildPk(buildId, Project(projectId, null, null)))

        if(!buildInfo.isPresent) {
            throw Exception("Build was not found")
        }

        val build = buildInfo.get()

        val licenses = ArrayList<DependencyLicense>()

        build.dependency!!.forEach {
            licenses.addAll(it.license)
        }

        model["licenses"] = licenses
        model["view_name"] = "licenses"

        return model
    }

    fun getProjectVulnerabilitiesView(project: Project) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val vulnerabilities = ArrayList<DependencyVulnerability>()

        project.build?.forEach {
            it.dependency!!.forEach {
                vulnerabilities.addAll(it.vulnerabilities)
            }
        }

        model["vulnerabilities"] = vulnerabilities
        model["view_name"] = "vulnerabilities"

        return model
    }

    fun getBuildVulnerabilitiesView(projectId: String, buildId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val buildInfo = buildRepo.findById(BuildPk(buildId, Project(projectId, null, null)))

        if(!buildInfo.isPresent) {
            throw Exception("Build was not found")
        }

        val build = buildInfo.get()

        val vulnerabilities = ArrayList<DependencyVulnerability>()

        build.dependency!!.forEach {
            vulnerabilities.addAll(it.vulnerabilities)
        }

        model["vulnerabilities"] = vulnerabilities
        model["view_name"] = "vulnerabilities"

        return model
    }


}