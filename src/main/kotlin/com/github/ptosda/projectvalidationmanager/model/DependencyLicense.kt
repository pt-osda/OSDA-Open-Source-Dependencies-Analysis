package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class DependencyLicense(
        @Id
        val license_spdx_id: String,

        @Id
        val dependency_id: Long,

        @Id
        val dependency_main_version: String,

        val source: String,

        @ManyToOne
        val dependency: Dependency,

        @ManyToOne
        val license: License
) : Serializable