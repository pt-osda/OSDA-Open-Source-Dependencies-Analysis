package com.github.ptosda.projectvalidationmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class ProjectValidationManagerApplication

    fun main(args: Array<String>) {
        runApplication<ProjectValidationManagerApplication>(*args)
    }