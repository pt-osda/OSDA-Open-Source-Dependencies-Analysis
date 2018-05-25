package com.github.ptosda.projectvalidationmanager.model.entities

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
data class DependencyLicense(

        @EmbeddedId
        val pk: DependencyLicensePk,

        val source: String

) : Serializable