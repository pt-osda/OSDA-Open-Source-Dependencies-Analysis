package com.github.ptosda.projectvalidationmanager.model.entities

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "dependency")
data class Dependency (

        @EmbeddedId
        val pk: DependencyPk,

        val description: String,

        val vulnerabilitiesCount: Int,

        @ManyToMany(cascade = [CascadeType.ALL])
        val dependencies: Set<Dependency>,

        @OneToMany(mappedBy = "pk.dependency")
        val license: List<DependencyLicense>,

        @OneToMany(mappedBy = "pk.dependency", cascade = [CascadeType.ALL])
        val vulnerabilities: List<DependencyVulnerability>
) : Serializable
{
        override fun equals(other: Any?): Boolean {
                var result = false
                if (other is Dependency) {
                        val otherObj = other as Dependency?
                        result = this.pk.id == otherObj!!.pk.id &&
                                this.pk.mainVersion == otherObj.pk.mainVersion &&
                                this.description == otherObj.description
                }
                return result
        }

        override fun hashCode(): Int {
                return this.pk.id.hashCode() * 41 +
                        this.pk.mainVersion.hashCode() * 17 +
                        this.description.hashCode() * 41
        }

        override fun toString(): String {
                return "id:" + pk.id + "; main_version:" + pk.mainVersion + "; description:" + description + "; vulnerabilitiesCount:" + vulnerabilitiesCount
        }
}