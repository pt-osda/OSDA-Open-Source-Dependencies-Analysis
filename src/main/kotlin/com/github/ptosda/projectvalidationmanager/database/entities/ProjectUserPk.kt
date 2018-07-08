package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.ManyToOne

@Embeddable
data class ProjectUserPk (
        @ManyToOne
        val project: Project,

        @ManyToOne
        val userInfo: User
) : Serializable
