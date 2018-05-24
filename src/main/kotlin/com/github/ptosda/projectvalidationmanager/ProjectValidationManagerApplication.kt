package com.github.ptosda.projectvalidationmanager

import com.github.ptosda.projectvalidationmanager.model.DependencyInfo
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder

@EnableCaching
@SpringBootApplication
class ProjectValidationManagerApplication

    fun main(args: Array<String>) {
        runApplication<ProjectValidationManagerApplication>(*args)
    }