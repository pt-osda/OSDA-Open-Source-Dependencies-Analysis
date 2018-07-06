package com.github.ptosda.projectvalidationmanager.controllers

import com.github.ptosda.projectvalidationmanager.model.report.Report
import com.github.ptosda.projectvalidationmanager.model.report.ReportDependency
import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import com.github.ptosda.projectvalidationmanager.model.report.ReportVulnerability
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors
import javax.swing.Action

@RestController
@RequestMapping("/report")
// TODO validate private versions on npm and return error
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

    data class DependencyVulnerabilityInputModel(val projectId: String, val reportId: String,
                                                 val dependencyId:String, val dependencyVersion: String,
                                                 val vulnerabilityId: Long)

    @PutMapping("dependency/vulnerability/edit")
    fun alterDependencyVulnerabilityState(@RequestBody dependencyVulnerabilityInput: DependencyVulnerabilityInputModel){
        val vulnerabilityInfo = vulnerabilityRepository.findById(dependencyVulnerabilityInput.vulnerabilityId)
        val dependencyInfo = dependencyRepository.findById(DependencyPk(dependencyVulnerabilityInput.dependencyId, Report(ReportPk(dependencyVulnerabilityInput.reportId, Project(dependencyVulnerabilityInput.projectId, null, null)), null, null), dependencyVulnerabilityInput.dependencyVersion))

        if(!dependencyInfo.isPresent) {
            throw Exception("Dependency not found")
        }

        if(!vulnerabilityInfo.isPresent) {
            throw Exception("Vulnerability not found")
        }

        val dependency = dependencyInfo.get()
        val vulnerability = vulnerabilityInfo.get()

        val dependencyVulnerabilityInfo = dependencyVulnerabilityRepository.findById(DependencyVulnerabilityPk(dependency, vulnerability))

        if(!dependencyVulnerabilityInfo.isPresent) {
            throw Exception("Dependency Vulnerability not found")
        }

        val dependencyVulnerability = dependencyVulnerabilityInfo.get()
        dependencyVulnerability.ignored = !dependencyVulnerability.ignored

        dependencyVulnerabilityRepository.save(dependencyVulnerability)
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
     *
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
     *
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
     *
     * Every dependency will be stored in the database as they are specific to each report.
     * @param dependencies The list of the dependencies referenced in the report that belong to the project
     * @param report The report this dependencies belong to.
     */
    private fun storeDependencies(dependencies: ArrayList<ReportDependency>, report: com.github.ptosda.projectvalidationmanager.database.entities.Report) {
        dependencies.forEach {
            val childrenSet : MutableSet<Dependency> = mutableSetOf()

            if (it.children != null && it.children.size > 0) {
                val children = it.children

                for (dependency in dependencies){
                    if (children.contains(dependency.title + ":" + dependency.mainVersion))
                        childrenSet.add(Dependency(DependencyPk(dependency.title, report, dependency.mainVersion),
                                dependency.description,
                                0,  // Children don't have vulnerabilities their parents do.
                                null,
                                emptySet(),
                                emptyList(),
                                arrayListOf(),
                                dependency.direct!!))
                }
            }

            val dependency = Dependency(
                    DependencyPk(it.title, report, it.mainVersion),
                    it.description,
                    0,
                    //it.privateVersions,
                    null,
                    childrenSet,
                    arrayListOf(),
                    arrayListOf(),
                    it.direct!!
            )

            dependencyRepository.save(dependency)
            logger.info("All the dependency regarded information was stored in the database.")

            storeDependencyLicenses(it, dependency)
            logger.info("All the dependency license regarded information was stored in the database.")
        }

        updateVulnerabilities(dependencies, report)
    }

    /**
     * Function to create the licenses referenced by the dependency of a project if it didn't already existed.
     *
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
                    it.source,
                    it.valid
            ))
        }
        dependencyLicenseRepository.saveAll(licenses)
    }

    // TODO add multiple sources
    /**
     * Function responsible for finding the vulnerabilities that the direct dependencies should have because of their
     * children.
     *
     * Here the dependencies graph will be traveled in order to find the direct dependency that needs the
     * vulnerable dependency.
     * @param dependencies The dependencies received from the report of the plugin.
     * @param report The report send by the plugin.
     */
    private fun updateVulnerabilities(dependencies: ArrayList<ReportDependency>, report: com.github.ptosda.projectvalidationmanager.database.entities.Report) {
        val vulnerableDependencies = dependencies.filter { it.vulnerabilitiesCount > 0 }

        val directDependencies = dependencyRepository.getDirectDependenciesFromReport(report.pk.project.name, report.pk.timestamp)

        vulnerableDependencies.forEach {
            val reportDependency = it
            val dependencyTitle = it.title
            val vulnerableVersions = getVulnerableDependency(reportDependency, it.mainVersion, it.privateVersions)

            vulnerableVersions.forEach {
                val dependencyId = "$dependencyTitle:$it"
                val upperDependencies = dependencies.filter { it.children != null && it.children.contains(dependencyId) }
                val parentDependency = upperDependencies.stream().filter { it.direct!! }.collect(Collectors.toList())

                if(reportDependency.direct!!){
                    parentDependency.add(reportDependency)
                }
                parentDependency.addAll(findDirectParent(upperDependencies, dependencies, parentDependency))

                saveParentsVulnerabilities(parentDependency.distinct(), directDependencies, reportDependency.vulnerabilitiesCount, reportDependency.vulnerabilities, dependencyId)
            }
            /*val dependencyId = it.title + ":" + it.mainVersion
            val upperDependencies = dependencies.filter { it.children != null && it.children.contains(dependencyId) }
            val parentDependency = upperDependencies.stream().filter { it.direct!! }.collect(Collectors.toList())

            if(it.direct!!){
                parentDependency.add(it)
            }
            parentDependency.addAll(findDirectParent(upperDependencies, dependencies, parentDependency))

            saveParentsVulnerabilities(parentDependency.distinct(), directDependencies, it.vulnerabilitiesCount, it.vulnerabilities, dependencyId)*/
        }
    }

    private fun getVulnerableDependency(reportDependency: ReportDependency, dependencyVersion: String, privateVersions: ArrayList<String>?): ArrayList<String> {
        val vulnerableVersions = ArrayList<String>()
        //val accessPrivateVersions = ArrayList<String>(privateVersions)
        reportDependency.vulnerabilities.forEach {
            if (it.versions.size > 1) {
                it.versions.forEach {
                    if (it == dependencyVersion)
                        vulnerableVersions.add(dependencyVersion)
                    else if (privateVersions != null && privateVersions.contains(it)) {
                        val vulnerableVersion = it
                        privateVersions.forEach {
                            if (it == vulnerableVersion)
                                vulnerableVersions.add(vulnerableVersion)
                        }
                    }
                }
            } else {
                val versions = it.versions[0].split("||")

                for (version in versions) {
                    val comparisons = version.split(" ")
                    var betweenLimits = true
                    for (comparableVersion in comparisons) {
                        when {
                            comparableVersion.contains(">") ->
                                betweenLimits = betweenLimits && compare(comparableVersion.replace(">", ""), dependencyVersion, { compareValue, limitValue -> compareValue > limitValue })

                            comparableVersion.contains(">=") ->
                                betweenLimits = betweenLimits && compare(comparableVersion.replace(">=", ""), dependencyVersion, { compareValue, limitValue -> compareValue >= limitValue })

                            comparableVersion.contains("<") ->
                                betweenLimits = betweenLimits && compare(comparableVersion.replace("<", ""), dependencyVersion, { compareValue, limitValue -> compareValue < limitValue })

                            comparableVersion.contains("<=") ->
                                betweenLimits = betweenLimits && compare(comparableVersion.replace("<=", ""), dependencyVersion, { compareValue, limitValue -> compareValue < limitValue })
                        }

                        if (privateVersions != null) {
                            var privateBetweenLimits = true
                            for (privateVersion in privateVersions) {
                                when {
                                    comparableVersion.contains(">") ->
                                        privateBetweenLimits = privateBetweenLimits && compare(comparableVersion.replace(">", ""), privateVersion, { compareValue, limitValue -> compareValue > limitValue })

                                    comparableVersion.contains(">=") ->
                                        privateBetweenLimits = privateBetweenLimits && compare(comparableVersion.replace(">=", ""), privateVersion, { compareValue, limitValue -> compareValue >= limitValue })

                                    comparableVersion.contains("<") ->
                                        privateBetweenLimits = privateBetweenLimits && compare(comparableVersion.replace("<", ""), privateVersion, { compareValue, limitValue -> compareValue < limitValue })

                                    comparableVersion.contains("<=") ->
                                        privateBetweenLimits = privateBetweenLimits && compare(comparableVersion.replace("<=", ""), privateVersion, { compareValue, limitValue -> compareValue < limitValue })
                                }

                                if (privateBetweenLimits) {
                                    vulnerableVersions.add(privateVersion)
                                }
                            }
                        }
                    }

                    if (betweenLimits && !vulnerableVersions.contains(dependencyVersion)) {
                        vulnerableVersions.add(dependencyVersion)
                    }
                }
            }
        }
        return vulnerableVersions
    }

    private fun compare(limitVersion: String, toCompareVersion: String, comparison: (compareValue: Int, limitValue: Int) -> Boolean): Boolean {
        val numLimitVersion = limitVersion.split("-")[0].split(".")
        val numCompareVersion = toCompareVersion.split("-")[0].split(".")
        var idx = 0
        while (idx < numLimitVersion.size) {
            val numLimit = numLimitVersion[idx].toInt()
            val numCompare = numCompareVersion[idx].toInt()
            if (numLimit == numCompare)
                idx++
            else return comparison(numCompare, numLimit)
        }
        return true
    }

    /**
     * Function that will recursively travel through the dependency graph beginning in the vulnerable dependencies until
     * it is found the direct dependency that uses the vulnerable one.
     *
     * @param upperDependencies The list of dependencies that have as children the vulnerable dependency.
     * @param dependencies The dependencies received from the report of the plugin.
     * @param parentDependency The list of direct dependencies that have as children the vulnerable one.
     * @return The list of direct dependencies that have as children the vulnerable one.
     */
    private fun findDirectParent(upperDependencies: List<ReportDependency>, dependencies: ArrayList<ReportDependency>, parentDependency: MutableList<ReportDependency>): MutableList<ReportDependency> {
        upperDependencies.stream().filter { !it.direct!! }.forEach {
            val dependencyId = it.title + ":" + it.mainVersion
            val dependencyParents = dependencies.filter { it.children!!.contains(dependencyId) }
            parentDependency.addAll(dependencyParents.stream().filter { it.direct!! && !parentDependency.contains(it) }.collect(Collectors.toList()))

            parentDependency.addAll(findDirectParent(dependencies.filter { it.children!!.contains(dependencyId) }, dependencies, parentDependency))
        }
        return parentDependency
    }

    /**
     * Saves in the database the vulnerabilities received in the report from the plugin.
     *
     * Saves the connection between the vulnerabilities and the direct dependencies that brought them.
     *
     * @param parents The list of parents that are vulnerable either directly or because one of their dependency.
     * @param directDependencies The list of direct dependencies refereed in the report.
     * @param vulnerabilitiesCount The number of vulnerabilities found.
     * @param vulnerabilities The list of vulnerabilities found.
     * @param vulnerableDependencyId The vulnerable dependency.
     */
    private fun saveParentsVulnerabilities(parents: List<ReportDependency>, directDependencies: List<Dependency>, vulnerabilitiesCount: Int, vulnerabilities: ArrayList<ReportVulnerability>, vulnerableDependencyId: String) {
        parents.forEach {
            val parent = it
            val direct = directDependencies.filter { it.pk.id == parent.title && it.pk.mainVersion == parent.mainVersion }[0]

            direct.vulnerabilitiesCount = direct.vulnerabilitiesCount?.plus(vulnerabilitiesCount)

            val toAddVulnerabilities = arrayListOf<DependencyVulnerability>()

            vulnerabilities.forEach {
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

                toAddVulnerabilities.add(DependencyVulnerability(
                        DependencyVulnerabilityPk(direct, vulnerability),
                        it.versions.joinToString(separator = ";"),
                        vulnerableDependencyId
                ))
            }

            dependencyVulnerabilityRepository.saveAll(toAddVulnerabilities)
        }
    }
}