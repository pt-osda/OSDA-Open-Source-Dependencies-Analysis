package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "build")
data class Build(
        @EmbeddedId
        val pk: BuildPK,

        val tag: String,

        //@OneToMany(mappedBy = "build")
        @ManyToMany
        val dependency: Set<Dependency>
) : Serializable
