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
                                     @RequestBody artifacts: ArrayList<Artifacts>) : ResponseEntity<ArrayList<VulnerabilitiesEvaluationOutput?>>
    {
        val dependenciesCache = CachingConfig.getDependenciesCache()
        val vulnerabilities = arrayOfNulls<VulnerabilitiesEvaluationOutput>(artifacts.size).toCollection(ArrayList())

        artifacts.forEachIndexed { index, artifact ->
            val dependencyName = if(artifact.group != null) "${artifact.name}:${artifact.group}" else artifact.name
            val cacheKey = "$manager:$dependencyName:${artifact.version}"
            val cacheEntry = dependenciesCache.get(cacheKey)
            if(cacheEntry?.vulnerabilities != null){
                logger.info("The dependency {} already had its vulnerabilities in cache", "$dependencyName:${artifact.version}")
                vulnerabilities[index] = cacheEntry.vulnerabilities!!
                artifact.inCache = true
            }
            artifact.index = index
        }

        artifacts.removeIf { it.inCache }
        if(!artifacts.isEmpty()) {
            logger.info("There are dependencies that need to search for vulnerabilities.")
            val vulnerabilitySearchResult = vulnerabilityService.getVulnerabilities(artifacts)

            if(vulnerabilitySearchResult == null) {
                logger.info("The external API could not be reached.")
                return ResponseEntity(HttpStatus.BAD_GATEWAY)
            }
            else {
                logger.info("The external API was successfully queried.")

                vulnerabilitySearchResult.forEachIndexed { index, vulnerabilityEvaluation ->
                    vulnerabilities[artifacts[index].index] = vulnerabilityEvaluation
                    val cacheKey = "$manager:${vulnerabilityEvaluation.title}:${vulnerabilityEvaluation.mainVersion}"
                    val cacheEntry = dependenciesCache.get(cacheKey)
                    if (cacheEntry == null) {
                        logger.info("The dependency was not in cache and it will be added.")
                        dependenciesCache.put(cacheKey, DependencyInfo(null, vulnerabilityEvaluation))   // TODO check if this is needed
                    } else {
                        logger.info("The dependency was in cache and it vulnerability information will be updated.")
                        cacheEntry.vulnerabilities = vulnerabilityEvaluation
                        dependenciesCache.put(cacheKey, cacheEntry)
                    }
                 }
            }
        }
        logger.info("The vulnerabilities search was successfully completed.")
        return ResponseEntity(vulnerabilities, HttpStatus.OK)
    }
}