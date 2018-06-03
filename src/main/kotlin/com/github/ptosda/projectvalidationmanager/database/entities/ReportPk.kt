package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Embeddable
class ReportPk(
        val timestamp: String,

        @ManyToOne
        @JoinColumn(name = "name")
        val project: Project
) : Serializable