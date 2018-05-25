package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable

data class DependencyInfo (var licenses : ArrayList<LicenseModel>?, var vulnerabilities : VulnerabilitiesEvaluationOutput?) : Serializable