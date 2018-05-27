package com.github.ptosda.projectvalidationmanager.model.entities

import com.github.ptosda.projectvalidationmanager.validators.interfaces.ValidRepo
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
@ValidRepo
data class Repo(
        @Id
        val name: String,

        val owner: String,

        @ManyToOne
        val organization: Organization,

        @OneToMany(mappedBy = "repo")
        val project: List<Project>
) : Serializable