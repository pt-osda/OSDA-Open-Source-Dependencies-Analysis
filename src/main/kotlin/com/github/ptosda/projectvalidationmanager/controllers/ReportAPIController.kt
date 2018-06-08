package com.github.ptosda.projectvalidationmanager.controllers

import com.github.ptosda.projectvalidationmanager.model.report.Report
import com.github.ptosda.projectvalidationmanager.model.report.ReportDependency
import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.xml.bind.DatatypeConverter

@RestController
@RequestMapping("/report")
class ReportAPIController(
        val organizationRepository: OrganizationRepository,
        val repoRepository: RepoRepository,
        val projectRepository: ProjectRepository,
        val reportRepository : ReportRepository,
        val dependencyLicenseRepository : DependencyLicenseRepository,
        val dependencyRepository : DependencyRepository,
        val dependencyVulnerabilityRepository : DependencyVulnerabilityRepository,
        val licenseRepository : LicenseRepository,
        val vulnerabilityRepository: VulnerabilityRepository){

    val logger : Logger = LoggerFactory.getLogger(ReportAPIController::class.java)

    @PostMapping
    fun postReport(@RequestBody report: Report) : ResponseEntity<Any> {
        logger.info("A new report has been received and will be stored in the database. {}", report.toString())

        var organization : Organization? = null
        var repo : Repo? = null

        if (report.organization != null) {
            logger.info("The organization {} will be created.", report.organization)
            organization = storeOrganization(report.organization)
        }

        if (report.repo != null && report.repoOwner != null) {
            logger.info("The repository named {} owned by {} will be created.", report.repo, report.repoOwner)
            repo = storeRepository(report.repo, report.repoOwner, organization)
        }
        logger.info("The project {} will be created.", report.name)
        val project = storeProject(report.name, repo)

        logger.info("The report created at {} and identified by {} will be created.", report.timestamp, report.buildTag)
        val generatedReport = storeReport(report.timestamp, report.buildTag, project)

        logger.info("The dependencies of the project will be created.")
        storeDependencies(report.dependencies, generatedReport)

        logger.info("The information from the report was successfully stored in the database.")
        return ResponseEntity(HttpStatus.CREATED)
    }

    /**
     * Function to create the organization referenced in the report if it didn't already existed.
     * @param organizationName The name of the organization to create.
     * @return The newly created organization or if it already existed the previously created one
     */
    private fun storeOrganization(organizationName : String): Organization {
        val organization : Organization
        val optionalOrganization = organizationRepository.findById(organizationName)
        if (!optionalOrganization.isPresent) {
            logger.info("The organization did not existed so a new one will be created.")
            organization = Organization(organizationName, emptyList())
            organizationRepository.save(organization)
        } else {
            logger.info("The organization already existed in the database.")
            organization = optionalOrganization.get()
        }

        logger.info("All the organization regarded information was stored in the database.")
        return organization
    }

    /**
     * Function responsible for creating the repository referenced in the report if it didn't already existed.
     * <br>
     * If the repository already existed and it didn't contain an organization and in the current report a organization
     * was referred, then that repository will be altered to reference the organization it belongs to.
     * @param repoName The name of the repository to create.
     * @param repoOwner The owner of the repository.
     * @param organization The organization to each the repository belongs to.
     * @return The newly created report or if it already existed the previously created one
     */
    private fun storeRepository(repoName: String, repoOwner: String, organization: Organization?): Repo {
        var repo : Repo
        val optionalRepo = repoRepository.findById(repoName)
        if (!optionalRepo.isPresent) {
            logger.info("The repository did not existed so a new one will be created")
            repo = Repo(repoName, repoOwner, organization, emptyList())
            repoRepository.save(repo)
        } else {
            logger.info("The repository already existed in the database.")
            repo = optionalRepo.get()
            if (repo.organization == null && organization != null){
                logger.info("The repository did not belonged to a organization but one was referenced in the report.")
                repo = Repo(repo.name, repo.owner, organization, repo.project)
                repoRepository.save(repo)
            }
        }
        logger.info("All the repository regarded information was stored in the database.")
        return repo
    }

    /**
     * Function for creating the project referenced in the report if it didn't already existed.
     * <br>
     * If the project already existed and it didn't referenced a repository and one was referenced in the report
     * then the project will be altered to reflect this change.
     * @param projectName The name of the project the report belongs to.
     * @param repo The repo to which the project belongs to.
     * @return The newly created project or if it already existed the previously created one
     */
    private fun storeProject(projectName: String, repo: Repo?) : Project {
        var project: Project
        if (!projectRepository.findById(projectName).isPresent) {
            logger.info("The project did not existed so a new one will be created.")
            project = Project(projectName, repo, listOf())
            projectRepository.save(project)
        } else {
            logger.info("The project already existed in the database.")
            project = projectRepository.findById(projectName).get()

            if (project.repo == null && repo != null){
                logger.info("The project did not belonged to a repository but one was referenced in the report.")
                project = Project(project.name, repo, project.report)
                projectRepository.save(project)
            }
        }
        logger.info("All the project regarded information was stored in the database.")
        return project
    }

    /**
     * Function to create the new report. Since every report occurs at a different time, there
     * is not the possibility to repeat reports.
     * @param timestamp The moment in time that the report was completed.
     * @param tag The tag that identifies the report.
     * @param project The project in which this report occurred.
     * @return The newly created report.
     */
    private fun storeReport(timestamp: String, tag: String?, project: Project): com.github.ptosda.projectvalidationmanager.database.entities.Report {
        val report = Report(ReportPk(timestamp, project), tag, setOf())
        reportRepository.save(report)
        logger.info("All the report regarded information was stored in the database")
        return report
    }

    /**
     * Function to create all the dependencies referenced in the report including their licenses and vulnerabilities.
     * Every dependency will be stored in the database as they are specific to each report.
     * @param dependencies The list of the dependencies referenced in the report that belong to the project
     * @param report The report this dependencies belong to.
     */
    private fun storeDependencies(dependencies: ArrayList<ReportDependency>, report: com.github.ptosda.projectvalidationmanager.database.entities.Report) {
        dependencies.forEach {
            val dependency = Dependency(
                    DependencyPk(it.title, report, it.mainVersion),
                    it.description,
                    it.vulnerabilitiesCount,
                    //it.privateVersions,
                    null,
                    setOf(),
                    arrayListOf(),
                    arrayListOf()
            )
            dependencyRepository.save(dependency)
            logger.info("All the dependency regarded information was stored in the database.")

            storeDependencyLicenses(it, dependency)
            logger.info("All the dependency license regarded information was stored in the database.")

            storeDependencyVulnerability(it, dependency)
            logger.info("All the dependency vulnerability regarded information was stored in the database.")
        }
    }

    /**
     * Function to create the licenses referenced by the dependency of a project if it didn't already existed.
     * <br>
     * Since the same license can be used for different dependencies there is only one entry for each license. So the
     * license is only created if it does not exist.
     * @param reportDependency The dependency in the report that contains the licenses from the report
     * @param dependency The dependency that will receive its licenses.
     */
    private fun storeDependencyLicenses(reportDependency: ReportDependency, dependency: Dependency) {
        val licenses = arrayListOf<DependencyLicense>()
        reportDependency.licenses.forEach {
            val license: License
            if (!licenseRepository.findById(it.spdxId).isPresent) {
                logger.info("The license did not existed so a new one will be created.")
                license = License(it.spdxId, null, listOf())    // TODO use errorInfo
                licenseRepository.save(license)
            } else {
                logger.info("The license already existed in the database.")
                license = licenseRepository.findById(it.spdxId).get()
            }
            licenses.add(DependencyLicense(
                    DependencyLicensePk(dependency, license),
                    it.source
            ))
        }
        dependencyLicenseRepository.saveAll(licenses)
    }

    /**
     * Function to create the vulnerabilities of a dependency of a project if it didn't already existed.
     * <br>
     * In case the vulnerability already exists there will not be changed anything.
     * @param reportDependency The dependency in the report that contains the vulnerabilities.
     * @param dependency The dependency that will have its vulnerabilities altered.
     */
    private fun storeDependencyVulnerability(reportDependency: ReportDependency, dependency: Dependency) {
        val vulnerabilities = arrayListOf<DependencyVulnerability>()
        reportDependency.vulnerabilities.forEach {
            val vulnerability: Vulnerability
            if (!vulnerabilityRepository.findById(it.id).isPresent) {
                logger.info("The vulnerability did not existed so a new one will be created.")
                vulnerability = Vulnerability(
                        it.id,
                        it.title,
                        it.description,
                        it.references,
                        setOf()
                )
                vulnerabilityRepository.save(vulnerability)
            } else {
                logger.info("The vulnerability already existed in the database.")
                vulnerability = vulnerabilityRepository.findById(it.id).get()   // TODO check if this is needed since a vulnerability is specific to a depenedency
            }
            vulnerabilities.add(DependencyVulnerability(
                    DependencyVulnerabilityPk(dependency, vulnerability),
                    it.versions.joinToString(separator = ";")
            ))
        }
        dependencyVulnerabilityRepository.saveAll(vulnerabilities)
    }
}