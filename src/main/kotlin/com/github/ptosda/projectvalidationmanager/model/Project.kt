package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import javax.persistence.*

@Entity
data class Project(
        @Id
        val name: String,

        @ManyToOne
        @JoinColumn(name = "repo_name", referencedColumnName = "name")
        val repo: Repo,

        @OneToMany(mappedBy = "pk.project")
        val build: List<Build>/*,

        @OneToMany(mappedBy = "project")
        val buildDependency: List<BuildDependency>*/
) : Serializable