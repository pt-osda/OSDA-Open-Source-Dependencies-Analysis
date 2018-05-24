package com.github.ptosda.projectvalidationmanager.model

import net.ossindex.common.VulnerabilityDescriptor
import java.io.Serializable

data class VulnerabilitiesEvaluation(
        val title: String,
        val mainVersion: String,
        val totalVulnerabilities: Int,
        val vulnerabilities: List<VulnerabilityDescriptor>?
) : Serializable