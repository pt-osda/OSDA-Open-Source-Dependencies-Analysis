package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.database.entities.*
import org.springframework.stereotype.Service

@Service
class ReportService
{
    fun getBuildDependencies(build: Build): Map<String, Any?> {
        val vulnerable = arrayListOf<Dependency>()
        val notVulnerable = arrayListOf<Dependency>()

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

    fun getDependencyVulnerabilities(dependencies: Set<Dependency>?): ArrayList<Any> {
        val vulnerabilities = ArrayList<Any>()

        dependencies?.forEach {
            vulnerabilities.addAll(it.vulnerabilities)
        }

        return vulnerabilities
    }
}
