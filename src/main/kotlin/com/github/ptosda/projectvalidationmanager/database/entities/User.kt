package com.github.ptosda.projectvalidationmanager.database.entities

import javax.persistence.*

@Entity
@Table(name = "userInfo")
data class User (
        val name: String,

        @Id
        val username: String,

        var password: String,


        @OneToMany(mappedBy = "pk.userInfo")
        val projects: List<ProjectUser>? = listOf(),

        @OneToOne
        var token : Token?
)