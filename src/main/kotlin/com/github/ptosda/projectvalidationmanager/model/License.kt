package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class License(
        @Id
        val spdx_id: String,

        val error_info: String,

        @OneToMany(mappedBy = "license")
        val dependencies: List<DependencyLicense>
) : Serializable