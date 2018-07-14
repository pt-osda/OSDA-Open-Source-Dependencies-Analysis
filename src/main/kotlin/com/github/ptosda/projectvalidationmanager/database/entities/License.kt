package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class License(
        @Id
        val spdxId: String,

        @OneToMany(mappedBy = "pk.license")
        val dependencies: List<DependencyLicense>
) : Serializable