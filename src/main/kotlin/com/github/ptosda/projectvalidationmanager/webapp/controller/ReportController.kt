package com.github.ptosda.projectvalidationmanager.webapp.controller

import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import com.github.ptosda.projectvalidationmanager.webapp.service.ReportService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import kotlin.collections.set

/**
 * Controller for the WebApp
 */
@Controller
@RequestMapping("/")
class ReportController(val reportService: ReportService,
                       val reportRepo: ReportRepository,
                       val projectRepo: ProjectRepository,
                       val dependencyRepo: DependencyRepository,
                       val vulnerabilityRepo: VulnerabilityRepository,
                       val licenseRepo: LicenseRepository) {
    /**
     * Gets the view for the home page
     */
    @GetMapping
    fun getHome(model: HashMap<String, Any>) : String
    {
        model["page_title"] = "Home"

        val projects = projectRepo.findAll()

        model["projects"] = projects

        return "home"
    }

    /**
     * Gets the view for the collection of dependencies
     */
    @GetMapping("deps")
    fun getDependencies(model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Dependencies"

        model["dependencies"] = dependencyRepo.findAll()
            .groupBy { it.pk.id + it.pk.mainVersion }
            .values
            .map { it.last() }
            .sortedBy{ it.pk.id.toLowerCase() }

        return "dependency-list"
    }

    /**
     * Gets the view of a dependency
     * @param dependencyId the id of the dependency to show
     * @param dependencyVersion the version of the dependency to show
     * TODO differentiate projects and detail of dependency
     */
    @GetMapping("deps/{dep-id}/version/{dep-version}")
    fun getDependencyGeneric(@PathVariable("dep-id") dependencyId : String,
                             @PathVariable("dep-version") dependencyVersion : String,
                             model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Dependency Detail"

        val dependency = dependencyRepo.findAll()
            .last { it.pk.mainVersion == dependencyVersion && it.pk.id == dependencyId }

        model["title"] = dependency.pk.id
        model["main_version"] = dependency.pk.mainVersion
        model["description"] = dependency.description
        model["license"] = dependency.license
        model["vulnerabilities"] = dependency.vulnerabilities
        model["projects"] = projectRepo.findAll()
            .filter { it.report?.last()?.dependency?.contains(dependency)!! }

        return "dependency-generic-detail"
    }

    /**
     * Gets the view for the collection of licenses
     */
    @GetMapping("licenses")
    fun getLicenses(model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Licenses"

        model["licenses"] = licenseRepo.findAll()

        return "license-list"
    }

    /**
     * Gets the view for the detail of a project
     * @param projectId the id of the project to show
     */
    @GetMapping("projs/{project-id}")
    fun getProjectDetail(@PathVariable("project-id") projectId: String,
                         model: HashMap<String, Any>) : String
    {
        model["page_title"] = "Project Reports"

        val builds = projectRepo.findById(projectId).get().report!!

        model["project_id"] = projectId
        model["reports"] = builds

        return "project"
    }

    /**
     * Gets the view for the detail of a report from a project
     * @param projectId the id of the project to search for a report
     * @param reportId the id of a report to show
     */
    @GetMapping("projs/{project-id}/report/{report-id}")
    fun getReportDetail(@PathVariable("project-id") projectId: String,
                        @PathVariable("report-id") reportId: String,
                        model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Report Detail"

        val reportInfo = reportRepo.findById(ReportPk(reportId, Project(projectId, null, null)))

        if(!reportInfo.isPresent) {
            throw Exception("Report was not found")
        }

        val report = reportInfo.get()

        model["project_id"] = projectId

        model["report_id"] = reportId
        model["report_tag"] = report.tag

        model.putAll(reportService.getReportDependencies(report))

        return "report"
    }

    /**
     * Gets the view for the detail of dependency from a report
     * @param projectId the id of a project
     * @param reportId the id of a report
     * @param dependencyId the id of a dependency
     * @param dependencyVersion the main version of a dependency
     * // TODO add transitive dependencies
     */
    @GetMapping("projs/{project-id}/report/{report-id}/deps/{dependency-id}/version/{dependency-version}")
    fun getDependencyDetail(@PathVariable("project-id") projectId: String,
                            @PathVariable("report-id") reportId: String,
                            @PathVariable("dependency-id") dependencyId: String,
                            @PathVariable("dependency-version") dependencyVersion: String,
                            model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Dependency Detail"

        val dependencyInfo = dependencyRepo.findById(DependencyPk(dependencyId, Report(ReportPk(reportId, Project(projectId, null, null)), null, null), dependencyVersion))

        if(!dependencyInfo.isPresent) {
            throw Exception("Dependency not found")
        }

        val dependency = dependencyInfo.get()

        model["project_id"] = projectId
        model["report_id"] = reportId

        model["title"] = dependency.pk.id
        model["main_version"] = dependency.pk.mainVersion
        model["description"] = dependency.description
        model["license"] = dependency.license
        model["vulnerabilities"] = dependency.vulnerabilities

        return "dependency-detail"
    }

    /**
     * Gets the view for the detail of a license from a report
     * @param projectId the id of a project
     * @param reportId the id of a report
     * @param licenseId the id of a license
     */
    @GetMapping("projs/{project-id}/report/{report-id}/licenses/{license-id}")
    fun getReportLicenseDetail(@PathVariable("project-id") projectId: String,
                               @PathVariable("report-id") reportId: String,
                               @PathVariable("license-id") licenseId: String,
                               model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "License Detail"

        val licenseInfo = licenseRepo.findById(licenseId)
        if(!licenseInfo.isPresent) {
            throw Exception("License not found")
        }
        val license = licenseInfo.get()

        model["license_id"] = license.spdxId
        model["dependencies"] = license.dependencies
                .filter{
                    it.pk.dependency.pk.report.pk.project.name == projectId && it.pk.dependency.pk.report.pk.timestamp == reportId
                }
        model["error_info"] = license.errorInfo

        return "license-detail"
    }

    /**
     * Gets the view for the detail of a license
     * @param licenseId the id of a license
     */
    @GetMapping("licenses/{license-id}")
    fun getLicenseDetail(@PathVariable("license-id") licenseId: String,
                         model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "License Detail"

        val licenseInfo = licenseRepo.findById(licenseId)

        if(!licenseInfo.isPresent) {
            throw Exception("License not found")
        }

        val license = licenseInfo.get()

        model["license_id"] = license.spdxId
        model["dependencies"] = license.dependencies
        model["error_info"] = license.errorInfo

        return "license-detail"
    }

    /**
     * Gets the view for the detail of a vulnerability
     * @param dependencyId the id of a dependency
     * @param vulnerabilityId the id of a vulnerability
     */
    @GetMapping("deps/{dependency-id}/vulnerability/{vulnerability-id}")
    fun getVulnerabilityDetail(@PathVariable("dependency-id") dependencyId: String,
                               @PathVariable("vulnerability-id") vulnerabilityId: Long,
                               model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Vulnerability Detail"

        val vulnerabilityInfo = vulnerabilityRepo.findById(vulnerabilityId)

        if(!vulnerabilityInfo.isPresent ) {
            throw Exception("License not found")
        }
        val vulnerability = vulnerabilityInfo.get()

        model["dependency_id"] = dependencyId

        model["vulnerability"] = vulnerability

        return "vulnerability-detail"
    }

}