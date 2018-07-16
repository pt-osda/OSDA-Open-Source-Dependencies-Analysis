package com.github.ptosda.projectvalidationmanager.webapp.service

import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.DependencyRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.ProjectRepository
import com.github.ptosda.projectvalidationmanager.database.repositories.ReportRepository
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class ReportFilterService {

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
        model["project"] = report.pk.project.id
        val reportLicenses = licenses
                .filter { it.pk.dependency.direct }
                .groupBy { it.pk.license.spdxId }
                .map { Pair(it.value[0], it.value.size) }
                .sortedByDescending { it.second }

        model["valid_licenses"] = reportLicenses.filter { it.first.valid }
        model["invalid_licenses"] = reportLicenses.filter { !it.first.valid }

        val vulnerabilities = ArrayList<DependencyVulnerability>()
        report.dependency?.forEach {
            vulnerabilities.addAll(it.vulnerabilities)
        }

        model["vulnerabilities"] = vulnerabilities

        return model
    }

    /**
     * Gets the model for the licenses of a report
     * @param report the report to filter
     */
    fun getReportLicensesView(report: Report) : HashMap<String, Any?> {
        val model = hashMapOf<String, Any?>()

        val licenses = ArrayList<DependencyLicense>()

        report.dependency!!.forEach {
            licenses.addAll(it.license)
        }

        model["report"] = report.pk.timestamp
        model["project"] = report.pk.project.id
        val reportLicenses = licenses
                .filter { it.pk.dependency.direct }
                .groupBy { it.pk.license.spdxId }
                .map { Pair(it.value[0], it.value.size) }
                .sortedByDescending { it.second }

        model["valid_licenses"] = reportLicenses.filter { it.first.valid }
        model["invalid_licenses"] = reportLicenses.filter { !it.first.valid }

        val vulnerabilities = ArrayList<DependencyVulnerability>()

        report.dependency.forEach {
            vulnerabilities.addAll(it.vulnerabilities)
        }

        model["vulnerabilities"] = vulnerabilities


        return model
    }
}