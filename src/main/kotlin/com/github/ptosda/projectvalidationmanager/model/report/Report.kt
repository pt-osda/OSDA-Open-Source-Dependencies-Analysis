package com.github.ptosda.projectvalidationmanager.model.report

import com.fasterxml.jackson.annotation.JsonProperty

data class Report (
        val id : String,
        val version : String?,
        val name : String,
        val description : String?,
        val timestamp : String,

        val organization : String?,

        val repo : String?,   // Indicates the name of the repository

        @JsonProperty("repo_owner")
        val repoOwner : String?,  // Indicates the owner of the repository

        val admin : String, // Indicates the username of the administrator of the project

        @JsonProperty("error_info")
        val errorInfo : String?, // Indicates the errors that occurred during report

        val dependencies : ArrayList<ReportDependency>,
        val successfulBuild : Boolean
)