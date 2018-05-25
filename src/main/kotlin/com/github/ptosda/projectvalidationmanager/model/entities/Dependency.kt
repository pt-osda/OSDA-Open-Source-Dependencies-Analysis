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

        @ManyToMany(mappedBy = "dependency")
        val build: Set<Build>,

        @OneToMany(mappedBy = "pk.dependency")
        val license: List<DependencyLicense>,

        @OneToMany(mappedBy = "pk.dependency")
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
}