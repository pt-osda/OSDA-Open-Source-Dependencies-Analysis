package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "dependency")
data class Dependency (
        /*@EmbeddedId
        val pk: DependencyPK,*/
        @Id
        val id: Long,

        @Id
        val main_version: String,

        val description: String,

        @OneToMany(mappedBy = "dependency")
        val dependencies: List<Dependencies>,

        @ManyToMany(mappedBy = "dependency")
        val build: Set<Build>,
        /*@OneToMany(mappedBy = "dependency")
        val build: List<BuildDependency>,*/

        @OneToMany(mappedBy = "dependency")
        val license: List<DependencyLicense>,

        @OneToMany(mappedBy = "dependency")
        val vulnerabilities: List<DependencyVulnerability>
) : Serializable