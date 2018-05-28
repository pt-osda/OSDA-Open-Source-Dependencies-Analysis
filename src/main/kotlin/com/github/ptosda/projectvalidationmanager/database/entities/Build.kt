package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "build")
data class Build(

        @EmbeddedId
        val pk: BuildPk,

        val tag: String?,

        @OneToMany(mappedBy = "pk.build")
        val dependency: Set<Dependency>?

) : Serializable
