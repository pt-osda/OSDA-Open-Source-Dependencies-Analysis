package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable

data class DependencyInfo (
        var licenses : ArrayList<LicenseModel>?,
        var licensesTimestamp : Long?,
        var vulnerabilities : VulnerabilitiesEvaluationOutput?,
        var vulnerabilitiesTimestamp : Long?
) : Serializable