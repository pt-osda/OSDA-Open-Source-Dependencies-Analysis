package com.github.ptosda.projectvalidationmanager.model

import java.io.Serializable
import java.time.Instant

data class DependencyInfo (
        var licenses : ArrayList<LicenseModel>?,
        var licensesTimestamp : Long?,
        var vulnerabilities : VulnerabilitiesEvaluationOutput?,
        var vulnerabilitiesTimestamp : Long?
) : Serializable