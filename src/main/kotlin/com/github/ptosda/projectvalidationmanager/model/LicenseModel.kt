package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable

data class LicenseModel(    // Sent by the API
        val name: String,
        val source: String
) : Serializable