package com.github.ptosda.projectvalidationmanager.model

import com.fasterxml.jackson.annotation.JsonInclude

data class Artifacts(
        val pm: String,
        val name: String,
        val version: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        val group: String?
)