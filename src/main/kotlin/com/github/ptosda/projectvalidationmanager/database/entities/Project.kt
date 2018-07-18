package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.*

@Entity
data class Project(
        @Id
        val id: String,

        val name: String?,

        val version: String?,

        val description: String?,

        @ManyToOne
        @JoinColumn(name = "repo_name")
        val repo: Repo?,

        @ManyToOne
        @JoinColumn(name = "admin_username", referencedColumnName = "username")
        val admin: User?,

        @OneToMany(mappedBy = "pk.project")
        val users: List<ProjectUser>?,

        @OneToMany(mappedBy = "pk.project")
        val report: List<Report>?,

        @OneToMany
        var ignoredVulnerabilities: MutableList<Vulnerability>?
) : Serializable
{
        override fun toString(): String {
                return "name:$name"
        }
}