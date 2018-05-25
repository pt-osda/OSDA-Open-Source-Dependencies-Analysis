package com.github.ptosda.projectvalidationmanager.model.entities

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "build")
data class Build(

        @EmbeddedId
        val pk: BuildPk,

        val tag: String,

        @ManyToMany
        val dependency: Set<Dependency>

) : Serializable
