package com.github.ptosda.projectvalidationmanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
        val userDetailsService: UserDetailsServiceImpl
) : WebSecurityConfigurerAdapter(){

    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()

    @Bean
    override fun authenticationManager() = super.authenticationManagerBean()

    override fun configure(http: HttpSecurity) {
        http
            .csrf()
                .ignoringAntMatchers("/{manager}/dependency/**", "/report", "/register")
                .and()
            .authorizeRequests()
                .antMatchers("/{manager}/dependency/**","/report", "/register", "/javascript/**", "/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                /*.loginPage("/login")
                .permitAll()*/
                .and()
            .logout()
                .permitAll()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder())
    }
}