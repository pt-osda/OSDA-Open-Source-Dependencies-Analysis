package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.*

@Entity
class Dependencies(
        @Id
        val id: Long,

        @Id
        val main_version: String,

        @Id
        val dependencies_id: Long,

        @Id
        val dependencies_main_version: String,

        @ManyToOne
        @JoinColumns(JoinColumn(name = "dependencies_id", referencedColumnName = "id"),
                JoinColumn(name = "dependencies_main_version", referencedColumnName = "main_version"))
        val dependency: Dependency
) : Serializable