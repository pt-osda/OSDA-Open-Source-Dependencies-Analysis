package com.github.ptosda.projectvalidationmanager

import com.github.ptosda.projectvalidationmanager.websecurity.AuthorizationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
data class MvcConfig(val interceptor: AuthorizationInterceptor) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor).addPathPatterns("/{manager}/dependency/**", "/report")
    }
}