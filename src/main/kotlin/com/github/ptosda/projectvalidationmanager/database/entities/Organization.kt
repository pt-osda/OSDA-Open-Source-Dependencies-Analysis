package com.github.ptosda.projectvalidationmanager.database.entities

import com.github.ptosda.projectvalidationmanager.validators.interfaces.ValidRepo
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
@ValidRepo
data class Organization(
        @Id
        val name: String,

        @OneToMany(mappedBy = "name")
        val repo: List<Repo>
) : Serializable