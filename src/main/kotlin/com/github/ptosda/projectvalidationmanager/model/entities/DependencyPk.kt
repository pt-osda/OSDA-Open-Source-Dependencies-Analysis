package com.github.ptosda.projectvalidationmanager.model.entities

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
class DependencyPk (

        val id: String,

        val mainVersion: String

) : Serializable