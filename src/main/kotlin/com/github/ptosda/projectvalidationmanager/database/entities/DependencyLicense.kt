package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class DependencyLicense(
        @EmbeddedId
        val pk: DependencyLicensePk,

        val source: String
) : Serializable {
    override fun equals(other: Any?): Boolean {
            if (other is DependencyLicense){
                    val dependencyLicense = other as DependencyLicense?
                    return dependencyLicense!!.pk.license.spdxId.equals(this.pk.license.spdxId)
            }
            return false
    }

    override fun hashCode(): Int {
        return this.pk.license.spdxId.hashCode() * 17
    }
}