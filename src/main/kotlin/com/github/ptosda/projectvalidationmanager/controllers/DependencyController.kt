package com.github.ptosda.projectvalidationmanager.controllers

import com.github.ptosda.projectvalidationmanager.CachingConfig
import com.github.ptosda.projectvalidationmanager.model.*
import com.github.ptosda.projectvalidationmanager.services.LicenseService
import com.github.ptosda.projectvalidationmanager.services.VulnerabilityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/{manager}/dependency")
class DependencyController(private val licenseService: LicenseService, private val vulnerabilityService: VulnerabilityService) {

    //TODO Need to do exception handling. Need to check licenseUrl
    @GetMapping("/{id}/{version}/licenses", produces = ["application/json"])
    fun getDependencyLicenses(@PathVariable("manager") manager: String,
                              @PathVariable("id") id: String,
                              @PathVariable("version") version: String,
                              @RequestParam(value="githubProject", required = false) githubProject: String?,
                              @RequestParam(value="licenseUrl", required = false) licenseUrl: String?): List<LicenseModel>
    {
        val cacheKey = "$manager:$id:$version"
        val dependenciesCache = CachingConfig.getDependenciesCache()
        val cacheEntry = dependenciesCache.get(cacheKey)
        if(cacheEntry?.licenses != null){
            return cacheEntry.licenses!!
        }
        val licenses = licenseService.findLicense(manager, id, version, licenseUrl!!)
        if(cacheEntry == null){
            dependenciesCache.put(cacheKey, DependencyInfo(licenses, null))
        }
        else{
            cacheEntry.licenses = licenses
            dependenciesCache.put(cacheKey, cacheEntry)
        }
        return licenses
    }

    @PostMapping("/vulnerabilities")
    fun getDependencyVulnerabilities(resp: HttpServletResponse,
                                     @PathVariable("manager") manager: String,
                                     @RequestBody artifacts: ArrayList<Artifacts>) : ResponseEntity<ArrayList<VulnerabilitiesEvaluationOutput>>
    {
        val dependenciesCache = CachingConfig.getDependenciesCache()
        val inCache = ArrayList<Artifacts>()
        val vulnerabilities = ArrayList<VulnerabilitiesEvaluationOutput>()

        artifacts.forEach {
            val dependencyName = if(it.group != null) "${it.name}:${it.group}" else it.name
            val cacheKey = "$manager:$dependencyName:${it.version}"
            val cacheEntry = dependenciesCache.get(cacheKey)
            if(cacheEntry?.vulnerabilities != null){
                vulnerabilities.add(cacheEntry.vulnerabilities!!)
                inCache.add(it)
            }
        }

        artifacts.removeAll(inCache)
        if(!artifacts.isEmpty()) {
            val vulnerabilitySearchResult = vulnerabilityService.getVulnerabilities(artifacts)

            if(vulnerabilitySearchResult == null)
                return ResponseEntity(HttpStatus.BAD_GATEWAY)
            else {
                vulnerabilities.addAll(vulnerabilitySearchResult)
                vulnerabilitySearchResult.forEach {
                    val cacheKey = "$manager:${it.title}:${it.mainVersion}"
                    val cacheEntry = dependenciesCache.get(cacheKey)
                    if (cacheEntry == null) {
                        dependenciesCache.put(cacheKey, DependencyInfo(null, it))
                    } else {
                        cacheEntry.vulnerabilities = it
                        dependenciesCache.put(cacheKey, cacheEntry)
                    }
                }
                return ResponseEntity(vulnerabilities, HttpStatus.OK)
            }
        }
        return ResponseEntity(vulnerabilities, HttpStatus.OK)
    }
}