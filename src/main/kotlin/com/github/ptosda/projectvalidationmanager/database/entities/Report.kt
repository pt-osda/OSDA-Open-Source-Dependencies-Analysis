package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "report")
data class Report(
        @EmbeddedId
        val pk: ReportPk,

        val tag: String?,

        @OneToMany(mappedBy = "pk.report")
        val dependency: Set<Dependency>? = setOf()
) : Serializable
{
    @Transient
    var vulnerabilitiesCount = 0
    get() = dependency!!.sumBy {
            it.vulnerabilitiesCount ?: 0
            }

    @Transient
    var vulnerableDependencies = 0
    get() = dependency!!.count {
                    if(it.vulnerabilitiesCount == null)
                    false
                    else
                    it.vulnerabilitiesCount > 0
            }

    @Transient
    var dependenciesCount = 0
        get() = dependency!!.size
}
