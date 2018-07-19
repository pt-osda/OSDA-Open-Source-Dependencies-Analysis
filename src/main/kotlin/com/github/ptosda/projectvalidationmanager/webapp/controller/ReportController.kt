package com.github.ptosda.projectvalidationmanager.webapp.controller

import com.github.ptosda.projectvalidationmanager.database.entities.DependencyPk
import com.github.ptosda.projectvalidationmanager.database.entities.Project
import com.github.ptosda.projectvalidationmanager.database.entities.Report
import com.github.ptosda.projectvalidationmanager.database.entities.ReportPk
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import com.github.ptosda.projectvalidationmanager.webapp.service.ReportFilterService
import com.github.ptosda.projectvalidationmanager.websecurity.service.SecurityServiceImpl
import com.github.ptosda.projectvalidationmanager.websecurity.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.time.ZonedDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.set

/**
 * Controller for the WebApp
 */
@Controller
@RequestMapping("/")
class ReportController(val userService: UserService,
                       val securityService: SecurityServiceImpl,
                       val projectUserRepo: ProjectUserRepository,
                       val reportRepo: ReportRepository,
                       val projectRepo: ProjectRepository,
                       val dependencyRepo: DependencyRepository,
                       val vulnerabilityRepo: VulnerabilityRepository,
                       val licenseRepo: LicenseRepository,
                       val reportFilterService: ReportFilterService)
{
    private final val PAGE_SIZE = 10

    /**
     * Gets the view for the home page
     * @param page the current page number
     * @param model the model for the response
     * @param req the HTTP request
     */
    @GetMapping
    fun getHome(@RequestParam(value = "page", defaultValue = "0") page: Int,
                model: HashMap<String, Any?>,
                req: HttpServletRequest) : String
    {
        model["page_title"] = "Home"

        val userName = securityService.findLoggedInUsername()!!
        model["username"] = userName

        model["error"] = req.getParameter("error")

        val order = Sort.Order(Sort.Direction.ASC, "name").ignoreCase()
        val currentPage = projectUserRepo.findAllByUsername(userName, PageRequest.of(page, PAGE_SIZE, Sort.by(order)))

        model["projects"] = currentPage.content
                .map { it.pk.project }

        if (currentPage.hasNext())
            model["next"] = currentPage.number + 1

        if (currentPage.hasPrevious())
            model["previous"] = currentPage.number - 1

        return "home"
    }

    /**
     * Gets the view for the collection of dependencies
     * @param page the current page number
     * @param model the model for the response
     */
    @GetMapping("deps")
    fun getDependencies(@RequestParam(value = "page", defaultValue = "0") page: Int,  model: HashMap<String, Any?>) : String
     {
        model["page_title"] = "Dependencies"

        val userName = securityService.findLoggedInUsername()!!

        val currentPage = dependencyRepo.findDistinctDirectDependencies(PageRequest.of(page, PAGE_SIZE))
        model["username"] = userName

        model["dependencies"] = currentPage

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
     * @param model the model for the response
     */
    @GetMapping("deps/{dep-id}/version/{dep-version}")
    fun getGenericDependency(@PathVariable("dep-id") dependencyId : String,
                             @PathVariable("dep-version") dependencyVersion : String,
                             model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Dependency Detail"

        val userName = securityService.findLoggedInUsername()!!

        model["username"] = userName

        val dependencies = dependencyRepo.findAll()
        val dependency = dependencies
                .last { it.pk.mainVersion == dependencyVersion && it.title == dependencyId }

        model["title"] = dependency.pk.id
        model["dependency_id"] = dependency.title
        model["main_version"] = dependency.pk.mainVersion
        model["private_versions"] = dependency.privateVersions
        model["description"] = dependency.description
        model["license"] = dependency.license
        model["vulnerabilities"] = dependency.vulnerabilities
        model["projects"] = projectRepo.findAll()
                .filter { it.report?.last()?.dependency?.contains(dependency)!! && it.users!!.stream().anyMatch { it.pk.userInfo.username == userName } }

        return "dependency/generic-dependency-detail"
    }

    /**
     * Gets the view for the collection of licenses
     * @param page the current page number
     * @param model the model for the response
     */
    @GetMapping("licenses")
    fun getLicenses(@RequestParam(value = "page", defaultValue = "0") page: Int, model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Licenses"

        val userName = securityService.findLoggedInUsername()
        model["username"] = userName

        val order = Sort.Order(Sort.Direction.ASC, "spdxId").ignoreCase()
        val currentPage = licenseRepo.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by(order)))
        model["licenses"] = currentPage

        if (currentPage.hasNext())
            model["next"] = currentPage.number + 1

        if (currentPage.hasPrevious())
            model["previous"] = currentPage.number - 1

        return "license/generic-license-list"
    }

    /**
     * Gets the view for the detail of a project
     * @param projectId the id of the project to show
     * @param model the model for the response
     */
    @Transactional
    @GetMapping("projs/{project-id}")
    fun getProjectDetail(@PathVariable("project-id") projectId: String,
                         model: HashMap<String, Any?>) : ModelAndView
    {
        model["page_title"] = "Project Reports"

        val projectInfo = projectRepo.findById(projectId)

        if(!projectInfo.isPresent) {
            throw Exception("Project not found")
        }

        val project = projectInfo.get()

        val userName = securityService.findLoggedInUsername()
        val user = userService.getUser(userName!!).get()

        if(!user.projects!!.any { it.pk.project!!.id == projectId }) {
            return ModelAndView("redirect:/?error", null)
        }
        model["username"] = user.username

        val reports = projectInfo.get().report

        model["is_admin"] = userName == project.admin!!.username
        model["associated_users"] = projectUserRepo.findAll()
                .filter{ it.pk.project!!.id == projectId }
                .map { it.pk.userInfo }

        model["project_id"] = projectId
        model["project_name"] = project.name
        model["project_version"] = project.version
        model["project_description"] = project.description
        model["repository"] = project.repo
        model["reports"] = reports!!.toList()
                .sortedByDescending{ ZonedDateTime.parse(it.pk.timestamp) }

        model.putAll(reportFilterService.getProjectLicensesAndVulnerabilities(project))

        return ModelAndView("project/project-detail", model)
    }

    /**
     * Gets the view for the detail of a report from a project
     * @param projectId the id of the project to search for a report
     * @param reportId the id of a report to show
     * @param model the model for the response
     */
    @GetMapping("projs/{project-id}/report/{report-id}")
    fun getReportDetail(@PathVariable("project-id") projectId: String,
                        @PathVariable("report-id") reportId: String,
                        model: HashMap<String, Any?>) : ModelAndView
    {
        model["page_title"] = "Report Detail"

        val reportInfo = reportRepo.findByProjectIdAndReportId(projectId, reportId)

        if(!reportInfo.isPresent) {
            throw Exception("Report was not found")
        }
        val report = reportInfo.get()

        val userName = securityService.findLoggedInUsername()
        val user = userService.getUser(userName!!).get()

        if(!user.projects!!.any { it.pk.project!!.id == projectId }) {
            return ModelAndView("redirect:/?error", null)
        }
        model["username"] = userName

        model["project_id"] = projectId
        model["project_name"] = report.pk.project.name
        model["error_info"] = if(report.error_info == null || report.error_info == "") null else report.error_info

        model["report_id"] = reportId
        model["readable_time"] = report.readableTimeStamp

        model["vulnerable_dependencies"] = report.dependency!!.filter {
            if(it.vulnerabilitiesCount == null)
                false
            else
                it.direct && it.vulnerabilities.count{!it.ignored} > 0
        }

        model.putAll(reportFilterService.getReportLicensesAndVulnerabilties(report))

        return ModelAndView("report/report-detail", model)
    }

    /**
     * Gets the view for the detail of dependency from a report
     * @param projectId the id of a project
     * @param reportId the id of a report
     * @param dependencyId the id of a dependency
     * @param dependencyVersion the main version of a dependency
     * @param model the model for the response
     */
    @GetMapping("projs/{project-id}/report/{report-id}/deps/{dependency-id}/version/{dependency-version}")
    fun getDependencyDetail(@PathVariable("project-id") projectId: String,
                            @PathVariable("report-id") reportId: String,
                            @PathVariable("dependency-id") dependencyId: String,
                            @PathVariable("dependency-version") dependencyVersion: String,
                            model: HashMap<String, Any?>) : ModelAndView
    {
        model["page_title"] = "Dependency Detail"

        val dependencyInfo = dependencyRepo.findById(DependencyPk(dependencyId, Report(ReportPk(reportId, Project(projectId, null, null, null, null, null, null, null, null)), null, false), dependencyVersion))

        if(!dependencyInfo.isPresent) {
            throw Exception("Dependency not found")
        }
        val dependency = dependencyInfo.get()


        val userName = securityService.findLoggedInUsername()

        val user = userService.getUser(userName!!).get()
        model["username"] = userName

        if(!user.projects!!.any { it.pk.project!!.id == projectId }) {
            return ModelAndView("redirect:/?error", null)
        }

        model["project_id"] = projectId
        model["report_id"] = reportId

        model["dependency_id"] = dependency.pk.id
        model["main_version"] = dependency.pk.mainVersion
        model["private_versions"] = dependency.privateVersions
        model["description"] = dependency.description
        model["license"] = dependency.license
        model["vulnerabilities"] = dependency.vulnerabilities

        return ModelAndView("dependency/dependency-detail", model)
    }

    /**
     * Gets the view for the detail of a license from a report
     * @param projectId the id of a project
     * @param reportId the id of a report
     * @param licenseId the id of a license
     * @param model the model for the response
     */
    @GetMapping("projs/{project-id}/report/{report-id}/licenses/{license-id}")
    fun getReportLicenseDetail(@PathVariable("project-id") projectId: String,
                               @PathVariable("report-id") reportId: String,
                               @PathVariable("license-id") licenseId: String,
                               model: HashMap<String, Any?>) : ModelAndView
    {
        model["page_title"] = "License Detail"

        val licenseInfo = licenseRepo.findById(licenseId)
        if(!licenseInfo.isPresent) {
            throw Exception("License not found")
        }
        val license = licenseInfo.get()

        val userName = securityService.findLoggedInUsername()

        val user = userService.getUser(userName!!).get()
        model["username"] = userName

        if(!user.projects!!.any { it.pk.project!!.id == projectId }) {
            return ModelAndView("redirect:/?error", null)
        }

        model["project_id"] = projectId
        model["report_id"] = reportId
        model["license_id"] = license.spdxId
        model["dependencies"] = license.dependencies
                .filter{
                    it.pk.dependency.pk.report.pk.project.id == projectId &&
                            it.pk.dependency.pk.report.pk.timestamp == reportId &&
                            it.pk.dependency.direct
                }

        return ModelAndView("license/license-detail", model)
    }

    /**
     * Gets the view for the detail of a license
     * @param licenseId the id of a license
     * @param model the model for the response
     */
    @GetMapping("licenses/{license-id}")
    fun getLicenseDetail(@PathVariable("license-id") licenseId: String,
                         model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "License Detail"
        model["username"] = securityService.findLoggedInUsername()

        val licenseInfo = licenseRepo.findById(licenseId)

        if(!licenseInfo.isPresent) {
            throw Exception("License not found")
        }

        val license = licenseInfo.get()

        model["license_id"] = license.spdxId
        model["dependencies"] = license.dependencies.distinctBy { it.pk.dependency.pk.id + it.pk.dependency.pk.mainVersion}

        return "license/generic-license-detail"
    }

    /**
     * Gets the view for the detail of a vulnerability
     * @param dependencyId the id of a dependency
     * @param vulnerabilityId the id of a vulnerability
     * @param model the model for the response
     */
    @GetMapping("deps/{dependency-id}/vulnerability/{vulnerability-id}")
    fun getVulnerabilityDetail(@PathVariable("dependency-id") dependencyId: String,
                               @PathVariable("vulnerability-id") vulnerabilityId: Long,
                               model: HashMap<String, Any?>) : ModelAndView
    {
        model["page_title"] = "Vulnerability Detail"

        val userName = securityService.findLoggedInUsername()

        val user = userService.getUser(userName!!).get()
        model["username"] = user.username

        val vulnerabilityInfo = vulnerabilityRepo.findById(vulnerabilityId)

        if(!vulnerabilityInfo.isPresent ) {
            throw Exception("Vulnerability not found")
        }
        val vulnerability = vulnerabilityInfo.get()

        model["dependency_id"] = dependencyId

        model["vulnerability"] = vulnerability

        return ModelAndView("vulnerability/vulnerability-detail", model)
    }
}