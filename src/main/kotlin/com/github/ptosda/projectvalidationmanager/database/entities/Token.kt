package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "token")
data class Token (
        @Id
        val token: String
) : Serializable