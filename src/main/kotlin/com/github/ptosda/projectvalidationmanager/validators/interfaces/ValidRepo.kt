package com.github.ptosda.projectvalidationmanager.validators.interfaces

import com.github.ptosda.projectvalidationmanager.validators.RepoValidator
import javax.validation.Constraint
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [RepoValidator::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidRepo constructor(
    val message: String = "Either groupName or owner must have a value",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Any>> = []
)