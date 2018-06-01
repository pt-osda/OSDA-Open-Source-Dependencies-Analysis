package com.github.ptosda.projectvalidationmanager.database.entities

import com.github.ptosda.projectvalidationmanager.validators.interfaces.ValidRepo
import java.io.Serializable
import javax.persistence.*

@Entity
@ValidRepo
data class Repo(
        @Id
        val name: String,

        val owner: String,

        @ManyToOne
        @JoinColumn(name="organization_name", referencedColumnName = "name")
        val organization: Organization?,

        @OneToMany(mappedBy = "repo")
        val project: List<Project>
) : Serializable