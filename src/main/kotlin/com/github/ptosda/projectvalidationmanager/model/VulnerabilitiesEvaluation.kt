package com.github.ptosda.projectvalidationmanager.model

data class VulnerabilitiesEvaluation(
        val title: String,
        val mainVersion: String,
        val totalVulnerabilities: Int,
        val vulnerabilities: List<Vulnerability>
)