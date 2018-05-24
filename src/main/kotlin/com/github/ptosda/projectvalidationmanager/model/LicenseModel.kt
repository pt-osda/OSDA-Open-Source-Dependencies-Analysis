package com.github.ptosda.projectvalidationmanager.model

data class LicenseModel(    // Send by the API
        val name: String,
        val sources: ArrayList<String>)