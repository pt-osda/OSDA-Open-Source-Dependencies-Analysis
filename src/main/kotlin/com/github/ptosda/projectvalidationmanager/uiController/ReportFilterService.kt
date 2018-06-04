package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.ReportRepository
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.stream.Collectors

@Service
class ReportFilterService(private val reportService: ReportService,
                          private val reportRepo: ReportRepository) {

    /**
     * Gets the model for the detail of a project
     * @param project the project to filter
     */
    fun getProjectDetailView(project: Project) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        model["project_id"] = project.name
        model["reports"] = project.report

        model["view_name"] = "project-detail"

        return model
    }

    /**
     * Gets the model for the detail of a report
     * @param projectId the id of the project
     * @param reportId the id of the report to filter
     */
    fun getBuildDetailView(projectId: String, reportId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val buildInfo = reportRepo.findById(ReportPk(reportId, Project(projectId, null, null)))

        if(!buildInfo.isPresent) {
            throw Exception("Report was not found")
        }

        val build = buildInfo.get()

        model["project_id"] = projectId

        model["report_id"] = reportId
        model["report_tag"] = build.tag

        model.putAll(reportService.getBuildDependencies(build))

        model["view_name"] = "report-detail"

        return model
    }

    /**
     * Gets the latest report of a project
     * @param project the project to search for the latest report
     */
    private fun getProjectLatestBuild(project: Project): Report {
        var latestBuild = project.report!!.first()
        var latestBuildDate = ZonedDateTime.parse(latestBuild.pk.timestamp)

        project.report.forEach{
            val currentBuildDate = ZonedDateTime.parse(it.pk.timestamp)
            if(latestBuildDate.isBefore(currentBuildDate)) {
                latestBuild = it
                latestBuildDate = currentBuildDate
            }
        }

        return latestBuild
    }

    /**
     * Gets the model for the licenses of a project. Only shows from the last report of the project
     * @param project the project to filter
     */
    fun getProjectLicensesView(project: Project) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()
        val licenses = ArrayList<DependencyLicense>()

        val build = getProjectLatestBuild(project)

        build.dependency?.forEach {
            licenses.addAll(it.license)
        }

        model["licenses"] = licenses
        model["view_name"] = "licenses"

        return model
    }

    /**
     * Gets the model for the licenses of a report
     * @param projectId the id of the project
     * @param reportId the id of the report to filter
     */
    fun getBuildLicensesView(projectId: String, reportId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val buildInfo = reportRepo.findById(ReportPk(reportId, Project(projectId, null, null)))

        if(!buildInfo.isPresent) {
            throw Exception("Report was not found")
        }

        val build = buildInfo.get()

        val licenses = ArrayList<DependencyLicense>()

        build.dependency!!.forEach {
            licenses.addAll(it.license)
        }

        val newLicenses = licenses.stream().distinct().collect(Collectors.toList())
        model["licenses"] = newLicenses
        model["view_name"] = "licenses"

        return model
    }

    /**
     * Gets the model for the vulnerabilities of a project. Only shows from the last report of the project
     * @param project the project to filter
     */
    fun getProjectVulnerabilitiesView(project: Project) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()
        val vulnerabilities = ArrayList<DependencyVulnerability>()

        val build = getProjectLatestBuild(project)

        build.dependency?.forEach {
            vulnerabilities.addAll(it.vulnerabilities)
        }

        model["vulnerabilities"] = vulnerabilities
        model["view_name"] = "vulnerabilities"

        return model
    }

    /**
     * Gets the model for the vulnerabilities of a report
     * @param projectId the id of the project
     * @param reportId the id of the report to filter
     */
    fun getBuildVulnerabilitiesView(projectId: String, reportId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val buildInfo = reportRepo.findById(ReportPk(reportId, Project(projectId, null, null)))

        if(!buildInfo.isPresent) {
            throw Exception("Report was not found")
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