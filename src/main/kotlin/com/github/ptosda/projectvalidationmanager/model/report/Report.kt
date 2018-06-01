package com.github.ptosda.projectvalidationmanager.model.report

import com.fasterxml.jackson.annotation.JsonProperty

data class Report (
        val id : String? = "ID",
        val version : String?,
        val name : String,
        val description : String?,
        val timestamp : String,

        @JsonProperty("build_tag")
        val buildTag : String?,

        val organization: String?,
        val repo: String?,   // Indicates the name of the repository
        val repoOwner: String?,  // Indicates the owner of the repository

        val dependencies : ArrayList<ReportDependency>
)