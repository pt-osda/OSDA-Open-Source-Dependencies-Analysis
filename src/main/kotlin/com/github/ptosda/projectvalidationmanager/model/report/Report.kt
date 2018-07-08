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

        val organization : String?,

        val repo : String?,   // Indicates the name of the repository

        @JsonProperty("repo_owner")
        val repoOwner : String?,  // Indicates the owner of the repository

        val admin : String, // Indicates the username of the administrator of the project

        val dependencies : ArrayList<ReportDependency>
)