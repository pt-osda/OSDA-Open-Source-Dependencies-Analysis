package com.github.ptosda.projectvalidationmanager.controllers

import com.github.ptosda.projectvalidationmanager.model.Report
import com.github.ptosda.projectvalidationmanager.model.ReportDependency
import com.github.ptosda.projectvalidationmanager.model.entities.*
import com.github.ptosda.projectvalidationmanager.model.repositories.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/report")
class ReportAPIController(val buildRepository : BuildRepository,
                       val dependencyLicenseRepository : DependencyLicenseRepository,
                       val dependencyRepository : DependencyRepository,
                       val dependencyVulnerabilityRepository : DependencyVulnerabilityRepository,
                       val licenseRepository : LicenseRepository,
                       val organizationRepository: OrganizationRepository,
                       val projectRepository: ProjectRepository,
                       val repoRepository: RepoRepository,
                       val vulnerabilityRepository: VulnerabilityRepository){

    @PostMapping
    fun postReport(@RequestBody report: Report) : ResponseEntity<Any> {
        val project = storeProject(report.name)
        val build = storeBuild(report.timestamp, report.buildTag, project)
        storeDependencies(report.dependencies, build)

        return ResponseEntity(HttpStatus.CREATED)
    }

    private fun storeProject(projectName: String) : Project {
        val project: Project
        if (!projectRepository.findById(projectName).isPresent) {
            project = Project(projectName, null, listOf())
            projectRepository.save(project)
        } else {
            project = projectRepository.findById(projectName).get()
        }
        return project
    }

    private fun storeBuild(timestamp: String, tag: String, project: Project): Build {
        val build = Build(BuildPk(timestamp, project), tag, setOf())
        buildRepository.save(build)
        return build
    }

    private fun storeDependencies(dependencies: ArrayList<ReportDependency>, build: Build) {
        dependencies.forEach {
            val dependency = Dependency(
                    DependencyPk(it.title, build, it.mainVersion),
                    it.description,
                    it.vulnerabilitiesCount,
                    //it.privateVersions,
                    setOf(),
                    arrayListOf(),
                    arrayListOf()
            )
            dependencyRepository.save(dependency)

            val licenses = arrayListOf<DependencyLicense>()
            it.licenses.forEach {
                val license: License
                if (!licenseRepository.findById(it.spdxId).isPresent) {
                    license = License(it.spdxId, null, listOf())
                    licenseRepository.save(license)
                } else {
                    license = licenseRepository.findById(it.spdxId).get()
                }
                licenses.add(DependencyLicense(
                        DependencyLicensePk(dependency, license),
                        it.source
                ))
            }
            dependencyLicenseRepository.saveAll(licenses)

            val vulnerabilities = arrayListOf<DependencyVulnerability>()
            it.vulnerabilities.forEach {
                val vulnerability: Vulnerability
                if (!vulnerabilityRepository.findById(it.id).isPresent) {
                    vulnerability = Vulnerability(
                            it.id,
                            it.title,
                            it.description,
                            it.references,
                            null,
                            setOf()
                    )
                    vulnerabilityRepository.save(vulnerability)
                } else {
                    vulnerability = vulnerabilityRepository.findById(it.id).get()
                }
                vulnerabilities.add(DependencyVulnerability(
                        DependencyVulnerabilityPk(dependency, vulnerability),
                        it.versions.joinToString(separator = ";")
                ))
            }
            dependencyVulnerabilityRepository.saveAll(vulnerabilities)
        }
    }

}