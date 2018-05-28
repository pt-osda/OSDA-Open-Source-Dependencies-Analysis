package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable

data class VulnerabilitiesEvaluationOutput(
        val title: String,
        val mainVersion : String,
        val totalVulnerabilities: Int,
        val vulnerabilities: List<VulnerabilityOutput> = ArrayList()
) : Serializable