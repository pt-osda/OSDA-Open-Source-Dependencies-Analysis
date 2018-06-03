package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "report")
data class Report(
        @EmbeddedId
        val pk: ReportPk,

        val tag: String?,

        @OneToMany(mappedBy = "pk.report")
        val dependency: Set<Dependency>?
) : Serializable
