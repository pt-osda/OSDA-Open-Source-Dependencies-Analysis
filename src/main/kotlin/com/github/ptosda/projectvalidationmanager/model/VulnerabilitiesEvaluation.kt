package com.github.ptosda.projectvalidationmanager.model

data class VulnerabilitiesEvaluation(val title: String,
                                     val mainVersion: String,
                                     val totalVulnerabilities: Int,
                                     val vulnerabilities: List<Vulnerability>)

data class Vulnerability(val title: String,
                         val id: Long,
                         val description: String,
                         val references: List<String>,
                         val versions: List<String>)