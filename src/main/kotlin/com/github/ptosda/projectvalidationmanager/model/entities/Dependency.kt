package com.github.ptosda.projectvalidationmanager.model.entities

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "dependency")
data class Dependency (

        @EmbeddedId
        val pk: DependencyPk,

        val description: String,

        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "dependencies",
                joinColumns= [JoinColumn(name="id", referencedColumnName = "id"), JoinColumn(name="main_version", referencedColumnName = "mainVersion")],
                inverseJoinColumns=[JoinColumn(name="dependencies.id", referencedColumnName = "id"), JoinColumn(name="dependencies.main_version", referencedColumnName = "mainVersion")])
        val dependencies: Set<Dependency>,


        @ManyToMany(mappedBy = "dependency")
        val build: Set<Build>,

        @OneToMany(mappedBy = "pk.dependency")
        val license: List<DependencyLicense>,

        @OneToMany(mappedBy = "pk.dependency")
        val vulnerabilities: List<DependencyVulnerability>
) : Serializable