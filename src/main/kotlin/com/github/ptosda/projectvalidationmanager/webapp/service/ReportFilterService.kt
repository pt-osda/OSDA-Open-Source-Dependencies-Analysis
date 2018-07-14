package com.github.ptosda.projectvalidationmanager.webapp.service

import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.DependencyRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.ProjectRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.ReportRepository
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class ReportFilterService(private val projectRepo: ProjectRepository,
                          private val reportService: ReportService,
                          private val reportRepo: ReportRepository,
                          private val dependencyRepo: DependencyRepository) {
    /**
     * Gets the model for the detail of a project
     * @param project the project to filter
     */
    fun getProjectDetailView(project: Project) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        model["project_id"] = project.name
        model["repository"] = project.repo
        model["reports"] = project.report?.sortedByDescending{ ZonedDateTime.parse(it.pk.timestamp) }

        model["view_name"] = "project/project-detail-partial"

        return model
    }

    /**
     * Gets the model for the detail of a report
     * @param projectId the id of the project
     * @param reportId the id of the report to filter
     */
    fun getReportDetailView(projectId: String, reportId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val reportInfo = reportRepo.findById(ReportPk(reportId, Project(projectId, null, null, null, null, null, null, null)))

        if(!reportInfo.isPresent) {
            throw Exception("Report was not found")
        }

        val report = reportInfo.get()

        model["project_id"] = projectId

        model["report_id"] = reportId
        model["report_tag"] = report.tag
        model["readable_time"] = report.readableTimeStamp

        model.putAll(reportService.getReportDependencies(report))

        model["view_name"] = "report/report-detail-partial"

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

        val report = getProjectLatestBuild(project)

        report.dependency?.forEach {
            licenses.addAll(it.license)
        }

        model["report"] = report.pk.timestamp
        model["project"] = report.pk.project.name
        val reportLicenses = licenses
                .filter { it.pk.dependency.direct }
                .groupBy { it.pk.license.spdxId }
                .map { Pair(it.value[0], it.value.size) }
                .sortedByDescending { it.second }

        model["valid_licenses"] = reportLicenses.filter { it.first.valid }
        model["invalid_licenses"] = reportLicenses.filter { !it.first.valid }

        model["view_name"] = "license/license-list-partial"

        return model
    }

    /**
     * Gets the model for the licenses of a report
     * @param projectId the id of the project
     * @param reportId the id of the report to filter
     */
    fun getReportLicensesView(projectId: String, reportId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val reportInfo = reportRepo.findById(ReportPk(reportId, Project(projectId, null, null, null, null, null, null, null)))

        if(!reportInfo.isPresent) {
            throw Exception("Report was not found")
        }

        val report = reportInfo.get()

        val licenses = ArrayList<DependencyLicense>()

        report.dependency!!.forEach {
            licenses.addAll(it.license)
        }

        model["report"] = reportId
        model["project"] = projectId
        val reportLicenses = licenses
                .filter { it.pk.dependency.direct }
                .groupBy { it.pk.license.spdxId }
                .map { Pair(it.value[0], it.value.size) }
                .sortedByDescending { it.second }

        model["valid_licenses"] = reportLicenses.filter { it.first.valid }
        model["invalid_licenses"] = reportLicenses.filter { !it.first.valid }

        model["view_name"] = "license/license-list-partial"

        return model
    }

    /**
     * Gets the model for the vulnerabilities of a project. Only shows from the last report of the project
     * @param project the project to filter
     */
    fun getProjectVulnerabilitiesView(project: Project) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()
        val vulnerabilities = ArrayList<DependencyVulnerability>()

        val report = getProjectLatestBuild(project)

        report.dependency?.forEach {
            vulnerabilities.addAll(it.vulnerabilities)
        }

        model["vulnerabilities"] = vulnerabilities
        model["view_name"] = "vulnerability/vulnerability-list-partial"

        return model
    }

    /**
     * Gets the model for the vulnerabilities of a report
     * @param projectId the id of the project
     * @param reportId the id of the report to filter
     */
    fun getReportVulnerabilitiesView(projectId: String, reportId: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val reportInfo = reportRepo.findById(ReportPk(reportId, Project(projectId, null, null, null, null, null, null, null)))

        if(!reportInfo.isPresent) {
            throw Exception("Report was not found")
        }

        val report = reportInfo.get()

        val vulnerabilities = ArrayList<DependencyVulnerability>()

        report.dependency!!.forEach {
            vulnerabilities.addAll(it.vulnerabilities)
        }

        model["vulnerabilities"] = vulnerabilities
        model["view_name"] = "vulnerability/vulnerability-list-partial"

        return model
    }

    /**
     * Gets the model for the vulnerabilities of a report
     * @param dependencyId the id of the dependency
     * @param dependencyVersion the version of the dependency
     */
    fun getGenericDependencyDetailView(dependencyId: String, dependencyVersion: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val dependency = dependencyRepo.findAll()
                .last { it.pk.mainVersion == dependencyVersion && it.pk.id == dependencyId }

        model["title"] = dependency.pk.id
        model["main_version"] = dependency.pk.mainVersion
        model["description"] = dependency.description
        model["license"] = dependency.license
        model["vulnerabilities"] = dependency.vulnerabilities

        model["view_name"] = "dependency/generic-dependency-detail-partial"

        return model
    }

    /**
     * Gets the model for the vulnerabilities of a report
     * @param dependencyId the id of the project
     * @param dependencyVersion the id of the report to filter
     */
    fun getGenericDependencyProjectsView(dependencyId: String, dependencyVersion: String) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val dependency = dependencyRepo.findAll()
                .last { it.pk.mainVersion == dependencyVersion && it.pk.id == dependencyId }

        model["projects"] = projectRepo.findAll()
                .filter { it.report?.last()?.dependency?.contains(dependency)!! }

        model["view_name"] = "project/project-list-partial"

        return model
    }
}