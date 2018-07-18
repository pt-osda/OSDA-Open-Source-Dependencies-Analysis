package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.*
import javax.xml.bind.DatatypeConverter

@Entity
@Table(name = "report")
data class Report(
        @EmbeddedId
        val pk: ReportPk,

        val error_info: String?,

        val successfulBuild: Boolean,

        @OneToMany(mappedBy = "pk.report")
        val dependency: Set<Dependency>? = setOf()
) : Serializable
{
    @Transient
    var vulnerabilitiesCount = 0
        get() {
            val vulnerabilities = mutableListOf<Vulnerability>()
            dependency?.forEach {
                it.vulnerabilities.forEach {
                    if (!vulnerabilities.contains(it.pk.vulnerability))
                        vulnerabilities.add(it.pk.vulnerability!!)
                }
            }
            return vulnerabilities.size
        }

    @Transient
    var vulnerableDependencies = 0
        get() = dependency!!.count {
            if(it.vulnerabilitiesCount == null)
                    false
                    else
                    it.vulnerabilitiesCount!! > 0
            }

    @Transient
    var dependenciesCount = 0
        get() = dependency!!.size

    @Transient
    var ignoredVulnerabilitiesCount = 0
        get() = dependency!!.sumBy {
            it.vulnerabilities.count{ it.ignored }
        }

    @Transient
    var readableTimeStamp = ""
        get() = DatatypeConverter.parseDateTime(pk.timestamp).time.toString()
}