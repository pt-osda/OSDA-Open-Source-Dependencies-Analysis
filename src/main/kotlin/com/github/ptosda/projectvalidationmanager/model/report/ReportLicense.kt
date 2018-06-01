package com.github.ptosda.projectvalidationmanager.model.report

import com.fasterxml.jackson.annotation.JsonProperty

data class ReportLicense(
        @JsonProperty("spdx_id")
        val spdxId : String,

        val source : String
)