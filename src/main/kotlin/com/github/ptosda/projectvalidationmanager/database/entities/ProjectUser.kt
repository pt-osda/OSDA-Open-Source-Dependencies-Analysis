package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class ProjectUser (
        @EmbeddedId
        val pk: ProjectUserPk
) : Serializable