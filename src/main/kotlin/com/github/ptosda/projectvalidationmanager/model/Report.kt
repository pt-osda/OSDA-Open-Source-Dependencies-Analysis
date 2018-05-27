package com.github.ptosda.projectvalidationmanager.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Report (
        val id : String,
        val version : String,
        val name : String,
        val description : String,
        val timestamp : String,
        val buildTag : String,
        val dependencies : ArrayList<ReportDependency>
)

data class ReportDependency(
        val title : String,
        val description: String,
        @JsonProperty("main_version") val mainVersion : String,
        @JsonProperty("private_versions") val privateVersions : ArrayList<String>?,
        val licenses : ArrayList<ReportLicense>,
        @JsonProperty("vulnerabilities_count") val vulnerabilitiesCount : Int,
        val vulnerabilities : ArrayList<ReportVulnerability>,
        val parents : ArrayList<ReportDependency>?
)

data class ReportLicense(
    @JsonProperty("spdx_id") val spdxId : String,
    val source : String
)

data class ReportVulnerability(
    val id : Long,
    val title : String,
    val description : String,
    val references : ArrayList<String>,
    val versions : ArrayList<String>
)