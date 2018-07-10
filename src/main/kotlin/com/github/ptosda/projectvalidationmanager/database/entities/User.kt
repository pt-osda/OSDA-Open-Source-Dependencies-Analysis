package com.github.ptosda.projectvalidationmanager.database.entities

import javax.persistence.*

@Entity
@Table(name = "userInfo")
data class User (
        @Id
        val username: String,

        var password: String,

        val name: String,

        @OneToMany(mappedBy = "pk.userInfo")
        val projects: List<ProjectUser>?,

        @OneToOne
        val token : Token
)