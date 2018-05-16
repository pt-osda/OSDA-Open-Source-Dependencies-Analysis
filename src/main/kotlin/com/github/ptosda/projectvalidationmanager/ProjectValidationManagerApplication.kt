package com.github.ptosda.projectvalidationmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProjectValidationManagerApplication

    fun main(args: Array<String>) {
        runApplication<ProjectValidationManagerApplication>(*args)
    }