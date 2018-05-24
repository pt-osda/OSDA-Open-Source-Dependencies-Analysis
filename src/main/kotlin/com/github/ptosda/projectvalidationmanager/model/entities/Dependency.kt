package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "dependency")
data class Dependency (
        @Id
        val id: Long,

        @Id
        val mainVersion: String,

        val description: String,

        @ManyToMany
        @JoinTable(name = "dependencies")
        val dependencies: Set<Dependency>,

        @ManyToMany(mappedBy = "dependency")
        val build: Set<Build>,

        @OneToMany(mappedBy = "dependency")
        val license: List<DependencyLicense>,

        @OneToMany(mappedBy = "dependency")
        val vulnerabilities: List<DependencyVulnerability>
) : Serializable