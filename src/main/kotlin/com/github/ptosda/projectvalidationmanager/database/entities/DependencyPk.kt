package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.*

@Embeddable
class DependencyPk (

        val id: String,

        @ManyToOne
        @JoinColumns(foreignKey = ForeignKey(name = "FK_Dependency_Build"), value = [
            JoinColumn(referencedColumnName = "timestamp", name = "timestamp"),
            JoinColumn(referencedColumnName = "name", name = "project")
        ])
        val build : Build,

        val mainVersion: String

) : Serializable