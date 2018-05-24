package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.*

//@Entity
data class BuildDependency(
        @Id
        val timestamp: String,

        @Id
        @ManyToOne
        @JoinColumn(name = "name")
        val project: Project,

        @Id
        val id: Long,

        @Id
        val main_version: String,

        @ManyToOne
        val build: Build,

        @ManyToOne
        val dependency: Dependency
) : Serializable