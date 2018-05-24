package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
data class DependencyPK(
        val id: Long,
        val main_version: String
) : Serializable