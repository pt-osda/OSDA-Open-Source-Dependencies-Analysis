package com.github.ptosda.projectvalidationmanager.webapp.controller

import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import com.github.ptosda.projectvalidationmanager.websecurity.service.SecurityServiceImpl
import com.github.ptosda.projectvalidationmanager.websecurity.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
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
                       val licenseRepo: LicenseRepository)
{

    private final val PAGE_SIZE = 10 //TODO filter user in search

    /**
     * Gets the view for the home page
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

        val currentPage = projectUserRepo.findAllByUsername(userName, PageRequest.of(page, PAGE_SIZE))

        model["projects"] = currentPage.content
                .map { it.pk.project }
                .sortedBy { it!!.name }

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

        model["page_title"] = "Dependencies"

        val userName = securityService.findLoggedInUsername()!!

        val currentPage = dependencyRepo.findAllByDirectOrderByPkAsc(true, PageRequest.of(page, PAGE_SIZE))
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
     * TODO differentiate projects and detail of dependency
     */
    @GetMapping("deps/{dep-id}/version/{dep-version}")
    fun getDependencyGeneric(@PathVariable("dep-id") dependencyId : String,
                             @PathVariable("dep-version") dependencyVersion : String,
                             model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Dependency Detail"

        val userName = securityService.findLoggedInUsername()!!

        model["username"] = userName

        //val decodedDepencencyId = dependencyId.replace(':', '/')

        val dependencies = dependencyRepo.findAll()
        val dependency = dependencies
                .last { it.pk.mainVersion == dependencyVersion && it.pk.id == dependencyId }

        model["title"] = dependency.pk.id
        model["dependency_id"] = dependency.title
        model["main_version"] = dependency.pk.mainVersion
        model["private_versions"] = dependency.privateVersions
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

        val userName = securityService.findLoggedInUsername()
        model["username"] = userName

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

        if(!user.projects!!.any { it.pk.project!!.name == projectId }) {
            return ModelAndView("redirect:/?error", null)
        }
        model["username"] = user.username

        val reports = projectInfo.get().report

        model["is_admin"] = userName == project.admin!!.username
        model["associated_users"] = projectUserRepo.findAll()
                .filter{ it.pk.project!!.name == projectId }
                .map { it.pk.userInfo }

        model["project_id"] = project.name
        model["repository"] = project.repo

        model["reports"] = reports!!.toList()
                .sortedByDescending{ ZonedDateTime.parse(it.pk.timestamp) }

        return ModelAndView("project/project-detail", model)
    }

    /**
     * Gets the view for the detail of a report from a project
     * @param projectId the id of the project to search for a report
     * @param reportId the id of a report to show
     */
    @GetMapping("projs/{project-id}/report/{report-id}")    // TODO avoid repeated request while selecting detail
    fun getReportDetail(@PathVariable("project-id") projectId: String,
                        @PathVariable("report-id") reportId: String,
                        model: HashMap<String, Any?>) : ModelAndView
    {
        model["page_title"] = "Report Detail"

        val reportInfo = reportRepo.findById(ReportPk(reportId, Project(projectId, null, null)))

        if(!reportInfo.isPresent) {
            throw Exception("Report was not found")
        }
        val report = reportInfo.get()

        val userName = securityService.findLoggedInUsername()
        val user = userService.getUser(userName!!).get()

        if(!user.projects!!.any { it.pk.project!!.name == projectId }) {
            return ModelAndView("redirect:/?error", null)
        }
        model["username"] = userName

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

        return ModelAndView("report/report-detail", model)
    }

    /**
     * Gets the view for the detail of dependency from a report
     * @param projectId the id of a project
     * @param reportId the id of a report
     * @param dependencyId the id of a dependency
     * @param dependencyVersion the main version of a dependency
     */
    @GetMapping("projs/{project-id}/report/{report-id}/deps/{dependency-id}/version/{dependency-version}")
    fun getDependencyDetail(@PathVariable("project-id") projectId: String,
                            @PathVariable("report-id") reportId: String,
                            @PathVariable("dependency-id") dependencyId: String,
                            @PathVariable("dependency-version") dependencyVersion: String,
                            model: HashMap<String, Any?>) : ModelAndView
    {
        model["page_title"] = "Dependency Detail"

        val dependencyInfo = dependencyRepo.findById(DependencyPk(dependencyId, Report(ReportPk(reportId, Project(projectId, null, null)), null, null), dependencyVersion))

        if(!dependencyInfo.isPresent) {
            throw Exception("Dependency not found")
        }
        val dependency = dependencyInfo.get()


        val userName = securityService.findLoggedInUsername()

        val user = userService.getUser(userName!!).get()
        model["username"] = userName

        if(!user.projects!!.any { it.pk.project!!.name == projectId }) {
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

        if(!user.projects!!.any { it.pk.project!!.name == projectId }) {
            return ModelAndView("redirect:/?error", null)
        }

        model["license_id"] = license.spdxId
        model["dependencies"] = license.dependencies
                .filter{
                    it.pk.dependency.pk.report.pk.project.name == projectId &&
                            it.pk.dependency.pk.report.pk.timestamp == reportId &&
                            it.pk.dependency.direct
                }
        model["error_info"] = license.errorInfo

        return ModelAndView("license/license-detail", model)
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

        val userName = securityService.findLoggedInUsername()

        val user = userService.getUser(userName!!).get()
        model["username"] = userName

        val licenseInfo = licenseRepo.findById(licenseId)

        if(!licenseInfo.isPresent) {
            throw Exception("License not found")
        }

        val license = licenseInfo.get()

        model["license_id"] = license.spdxId
        val dependencies = license.dependencies.filter { it.pk.dependency.direct }
        model["dependencies"] = dependencies.filter({ dep -> user.projects!!.any { it.pk.project!!.name == dep.pk.dependency.pk.report.pk.project.name } })
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

        if(!user.projects!!.any {
                    it.pk.project!!.report!!.any {
                        it.dependency!!.any {
                            it.title == dependencyId
                        }
                    }
                }
        ) {
            return ModelAndView("redirect:/?error", null)
        }

        model["dependency_id"] = dependencyId

        model["vulnerability"] = vulnerability

        return ModelAndView("vulnerability/vulnerability-detail", model)
    }
}