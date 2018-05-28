package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.ManyToOne

@Embeddable
class DependencyLicensePk (

        @ManyToOne
        val dependency: Dependency,

        @ManyToOne
        val license: License

) : Serializable