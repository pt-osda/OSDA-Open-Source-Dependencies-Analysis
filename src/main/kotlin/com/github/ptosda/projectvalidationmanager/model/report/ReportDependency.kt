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
        val children : ArrayList<String>?,
        val direct : Boolean?
) {
        override fun equals(other: Any?): Boolean {
                if (other is ReportDependency) {
                        return this.title == other.title &&
                                this.mainVersion === other.mainVersion
                }
                return false
        }

        override fun hashCode(): Int {
                return this.title.hashCode() * 3 +
                        this.mainVersion.hashCode() * 5
        }
}