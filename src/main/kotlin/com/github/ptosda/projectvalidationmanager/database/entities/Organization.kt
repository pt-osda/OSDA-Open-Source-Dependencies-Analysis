package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Organization(
        @Id
        val name: String,

        @OneToMany(mappedBy = "organization")
        val repo: List<Repo>?
) : Serializable
{
        override fun toString(): String {
                return "name:$name"
        }
}