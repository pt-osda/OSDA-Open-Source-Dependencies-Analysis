package com.github.ptosda.projectvalidationmanager.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Artifacts(
        val pm: String,
        val name: String,
        val version: String,
        @JsonIgnore val group: String
)