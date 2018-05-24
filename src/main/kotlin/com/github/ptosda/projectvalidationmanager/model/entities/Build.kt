package com.github.ptosda.projectvalidationmanager.model.entities

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "build")
data class Build(
        @Id
        val timestamp: String,

        @Id
        @ManyToOne
        @JoinColumn(name = "name")
        val project: Project,

        val tag: String,

        @ManyToMany
        val dependency: Set<Dependency>
) : Serializable
