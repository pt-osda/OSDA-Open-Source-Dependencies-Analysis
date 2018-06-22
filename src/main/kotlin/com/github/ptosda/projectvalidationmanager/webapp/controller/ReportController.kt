package com.github.ptosda.projectvalidationmanager.webapp.controller

import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import com.github.ptosda.projectvalidationmanager.webapp.service.ReportService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.ZonedDateTime
import java.util.*
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
                       val licenseRepo: LicenseRepository)
{

    private final val PAGE_SIZE = 10

    /**
     * Gets the view for the home page
     */
    @GetMapping
    fun getHome(@RequestParam(value = "page", defaultValue = "0") page: Int, model: HashMap<String, Any>) : String
    {
        val currentPage = projectRepo.findAll(PageRequest.of(page, PAGE_SIZE))

        model["page_title"] = "Home"

        val projects = currentPage
                .sortedBy{ it.name}

        model["projects"] = projects

        if (currentPage.hasNext())
            model["next"] = currentPage.number + 1

        if (currentPage.hasPrevious())
            model["previous"] = currentPage.number - 1

        return "home"
    }

    /**
     * Gets the view for the collection of dependencies
     */
    @GetMapping("deps")
    fun getDependencies(@RequestParam(value = "page", defaultValue = "0") page: Int,  model: HashMap<String, Any?>) : String
    {
        val currentPage = dependencyRepo.findAllByDirect(true, PageRequest.of(page, PAGE_SIZE))

        model["page_title"] = "Dependencies"

        model["dependencies"] = currentPage
                .groupBy { it.pk.id + it.pk.mainVersion }
                .values
                .map { it.last() }
                .sortedBy{ it.pk.id.toLowerCase() }

        if (currentPage.hasNext())
            model["next"] = currentPage.number + 1

        if (currentPage.hasPrevious())
            model["previous"] = currentPage.number - 1

        return "dependency/dependency-list"
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

        //val decodedDepencencyId = dependencyId.replace(':', '/')

        val dependencies = dependencyRepo.findAll()
        val dependency = dependencies
                .last { it.pk.mainVersion == dependencyVersion && it.pk.id == dependencyId }

        model["title"] = dependency.pk.id
        model["dependency_id"] = dependency.title
        model["main_version"] = dependency.pk.mainVersion
        model["description"] = dependency.description
        model["license"] = dependency.license
        model["vulnerabilities"] = dependency.vulnerabilities
        model["projects"] = projectRepo.findAll()
                .filter { it.report?.last()?.dependency?.contains(dependency)!! }

        return "dependency/generic-dependency-detail"
    }

    /**
     * Gets the view for the collection of licenses
     */
    @GetMapping("licenses")
    fun getLicenses(@RequestParam(value = "page", defaultValue = "0") page: Int, model: HashMap<String, Any?>) : String
    {
        val currentPage = licenseRepo.findAll(PageRequest.of(page, PAGE_SIZE))

        model["page_title"] = "Licenses"

        model["licenses"] = currentPage
            .sortedBy { it.spdxId }

        if (currentPage.hasNext())
            model["next"] = currentPage.number + 1

        if (currentPage.hasPrevious())
            model["previous"] = currentPage.number - 1

        return "license/generic-license-list"
    }

    /**
     * Gets the view for the detail of a project
     * @param projectId the id of the project to show
     */
    @Transactional
    @GetMapping("projs/{project-id}")
    fun getProjectDetail(@PathVariable("project-id") projectId: String,
                         model: HashMap<String, Any>) : String
    {
        model["page_title"] = "Project Reports"

        val reports = projectRepo.findById(projectId).get().report!!

        model["project_id"] = projectId

        reports.toList().forEach {
            val vulnerabilities = mutableListOf<Vulnerability>()
            it.dependency?.forEach { it.vulnerabilities.forEach {
                if (!vulnerabilities.contains(it.pk.vulnerability))
                    vulnerabilities.add(it.pk.vulnerability!!)
            } }
            it.vulnerabilitiesCount = vulnerabilities.size
        }

        model["reports"] = reports.toList()
                .sortedByDescending{ ZonedDateTime.parse(it.pk.timestamp) }

        return "project/project-detail"
    }

    /**
     * Gets the view for the detail of a report from a project
     * @param projectId the id of the project to search for a report
     * @param reportId the id of a report to show
     */
    @GetMapping("projs/{project-id}/report/{report-id}")    // TODO avoid repeated request while selecting detail
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
        model["readable_time"] = report.readableTimeStamp
        model["report_tag"] = report.tag


        model["vulnerable_dependencies"] = report.dependency!!.filter {
            if(it.vulnerabilitiesCount == null)
                false
            else
                it.vulnerabilitiesCount!! > 0 && it.direct
        }

        return "report/report-detail"
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
        model["dependency_id"] = dependency.title
        model["main_version"] = dependency.pk.mainVersion
        model["description"] = dependency.description
        model["license"] = dependency.license
        model["vulnerabilities"] = dependency.vulnerabilities

        return "dependency/dependency-detail"
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
                    it.pk.dependency.pk.report.pk.project.name == projectId &&
                            it.pk.dependency.pk.report.pk.timestamp == reportId &&
                            it.pk.dependency.direct
                }
        model["error_info"] = license.errorInfo

        return "license/license-detail"
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
        model["dependencies"] = license.dependencies.filter { it.pk.dependency.direct }
        model["error_info"] = license.errorInfo

        return "license/license-detail"
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

        return "vulnerability/vulnerability-detail"
    }
}