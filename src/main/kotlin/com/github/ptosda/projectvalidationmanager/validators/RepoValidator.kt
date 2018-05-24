package com.github.ptosda.projectvalidationmanager.validators

import com.github.ptosda.projectvalidationmanager.model.Repo
import com.github.ptosda.projectvalidationmanager.validators.interfaces.ValidRepo
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class RepoValidator : ConstraintValidator<ValidRepo, Repo>{
    override fun isValid(value: Repo?, context: ConstraintValidatorContext?): Boolean {
        return !(value?.organization == null && value?.owner == null)
    }
}