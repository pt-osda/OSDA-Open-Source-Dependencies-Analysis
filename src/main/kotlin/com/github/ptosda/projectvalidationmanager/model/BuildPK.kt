package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Embeddable
data class BuildPK(
        val timestamp: String,

        @ManyToOne
        @JoinColumn(name = "name")
        val project: Project
) : Serializable