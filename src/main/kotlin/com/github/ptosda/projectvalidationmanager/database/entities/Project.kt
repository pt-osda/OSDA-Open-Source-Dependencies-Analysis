package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.*

@Entity
data class Project(
        @Id
        val name: String,

        @ManyToOne
        @JoinColumn(name = "repo_name", referencedColumnName = "name")
        val repo: Repo?,

        @ManyToOne
        @JoinColumn(name = "admin_username", referencedColumnName = "username")
        val admin: User?,

        @OneToMany(mappedBy = "pk.project")
        val users: List<ProjectUser>? = arrayListOf(),

        @OneToMany(mappedBy = "pk.project")
        val report: List<Report>? = arrayListOf()
) : Serializable
{
        override fun toString(): String {
                return "name:$name"
        }
}