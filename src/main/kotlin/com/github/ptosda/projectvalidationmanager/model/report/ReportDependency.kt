package com.github.ptosda.projectvalidationmanager.model.report

import com.fasterxml.jackson.annotation.JsonProperty

data class ReportDependency(
        val title : String,
        val description: String?,

        @JsonProperty("main_version")
        val mainVersion : String,

        @JsonProperty("private_versions")
        val privateVersions : ArrayList<String>?,

        val licenses : ArrayList<ReportLicense>,

        @JsonProperty("vulnerabilities_count")
        val vulnerabilitiesCount : Int,

        val vulnerabilities : ArrayList<ReportVulnerability>,
        val parents : ArrayList<String>?
)