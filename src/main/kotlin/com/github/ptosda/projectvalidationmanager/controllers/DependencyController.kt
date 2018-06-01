package com.github.ptosda.projectvalidationmanager.controllers

import com.github.ptosda.projectvalidationmanager.CachingConfig
import com.github.ptosda.projectvalidationmanager.model.*
import com.github.ptosda.projectvalidationmanager.services.LicenseService
import com.github.ptosda.projectvalidationmanager.services.VulnerabilityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/{manager}/dependency")
class DependencyController(private val licenseService: LicenseService, private val vulnerabilityService: VulnerabilityService) {
    val logger : Logger = LoggerFactory.getLogger(DependencyController::class.java)

    @GetMapping("/{id}/{version}/licenses", produces = ["application/json"])
    fun getDependencyLicenses(@PathVariable("manager") manager: String,
                              @PathVariable("id") id: String,
                              @PathVariable("version") version: String,
                              @RequestParam(value="licenseUrl", required = true) licenseUrl: String): List<LicenseModel>
    {
        val cacheKey = "$manager:$id:$version"
        val dependenciesCache = CachingConfig.getDependenciesCache()
        val cacheEntry = dependenciesCache.get(cacheKey)

        if(cacheEntry?.licenses != null) {
            logger.info("The licenses of this dependency were already searched and are contained in cache.")
            return cacheEntry.licenses!!
        }

        val licenses = licenseService.findLicense(id, version, licenseUrl)

        if(!licenses.isEmpty()) {
            if (cacheEntry == null) {
                logger.info("The licenses found will be added to the cache")
                dependenciesCache.put(cacheKey, DependencyInfo(licenses, null))
            } else {
                logger.info("The licenses were already present in cache")
                cacheEntry.licenses = licenses
                dependenciesCache.put(cacheKey, cacheEntry)
            }
        }
        logger.info("The licenses found will be returned.")
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
                logger.info("The dependency {} already had its vulnerabilities in cache", "$dependencyName:${it.version}")
                vulnerabilities.add(cacheEntry.vulnerabilities!!)
                inCache.add(it)
            }
        }

        artifacts.removeAll(inCache)
        if(!artifacts.isEmpty()) {
            logger.info("The are dependencies that need to search for vulnerabilities.")
            val vulnerabilitySearchResult = vulnerabilityService.getVulnerabilities(artifacts)

            if(vulnerabilitySearchResult == null) {
                logger.info("The external API could not be reached.")
                return ResponseEntity(HttpStatus.BAD_GATEWAY)
            }
            else {
                logger.info("The external API was successfully queried.")
                vulnerabilities.addAll(vulnerabilitySearchResult)

                vulnerabilitySearchResult.forEach {
                    val cacheKey = "$manager:${it.title}:${it.mainVersion}"
                    val cacheEntry = dependenciesCache.get(cacheKey)
                    if (cacheEntry == null) {
                        logger.info("The dependency was not in cache and it will be added.")
                        dependenciesCache.put(cacheKey, DependencyInfo(null, it))   // TODO check if this is needed
                    } else {
                        logger.info("The dependency was in cache and it vulnerability information will be updated.")
                        cacheEntry.vulnerabilities = it
                        dependenciesCache.put(cacheKey, cacheEntry)
                    }
                }
                logger.info("The vulnerabilities search was successfully completed.")
                return ResponseEntity(vulnerabilities, HttpStatus.OK)
            }
        }
        logger.info("The vulnerabilities search was successfully completed.")
        return ResponseEntity(vulnerabilities, HttpStatus.OK)
    }
}