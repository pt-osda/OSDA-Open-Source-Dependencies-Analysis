package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class DependencyLicense(
        @EmbeddedId
        val pk: DependencyLicensePk,

        val source: String
) : Serializable