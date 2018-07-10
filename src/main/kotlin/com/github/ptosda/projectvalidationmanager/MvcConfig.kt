package com.github.ptosda.projectvalidationmanager

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

@Configuration
data class MvcConfig(val interceptor: AuthorizationInterceptor) : WebMvcConfigurationSupport() {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor).addPathPatterns("/{manager}/dependency/**", "/report/**")
    }
}