package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.database.entities.*
import org.springframework.stereotype.Service

@Service
class ReportService
{
    fun getBuildDependencies(build: Build): Map<String, List<Any>> {
        val vulnerable = arrayListOf<Any>()
        val notVulnerable = arrayListOf<Any>()

        build.dependency?.forEach {
            if(it.vulnerabilitiesCount == 0) {
                notVulnerable.add(it)
            }
            else {
                vulnerable.add(it)
            }
        }

        return hashMapOf("vulnerable_dependencies" to vulnerable,
                         "dependencies" to notVulnerable)
    }

    fun getBuildLicenses(build: Build): ArrayList<Any> {
        val licenses = ArrayList<Any>()

        build.dependency?.forEach {
            licenses.addAll(it.license)
        }

        return licenses
    }

    fun getDependencyVulnerabilities(dependencies: List<Dependency>?): ArrayList<Any> {
        val vulnerabilities = ArrayList<Any>()

        dependencies?.forEach {
            vulnerabilities.addAll(it.vulnerabilities)
        }

        return vulnerabilities
    }
}
