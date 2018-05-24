package com.github.ptosda.projectvalidationmanager.controllers

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.ptosda.projectvalidationmanager.model.LicenseModel
import com.github.ptosda.projectvalidationmanager.services.LicenseService
import com.github.ptosda.projectvalidationmanager.services.VulnerabilityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.xml.ws.Response

@RestController
@RequestMapping("/{manager}/dependency")
class DependencyController(private val licenseService: LicenseService, private val vulnerabilityService: VulnerabilityService) {

    //TODO Need to do exception handling. Need to check licenseUrl
    @GetMapping("/licenses/{id}/{version}", produces = ["application/json"])
    fun getDependencyLicenses(@PathVariable("manager") manager: String,
                              @PathVariable("id") id: String,
                              @PathVariable("version") version: String,
                              @RequestParam(value="githubProject", required = false) githubProject: String?,
                              @RequestParam(value="licenseUrl", required = false) licenseUrl: String?): List<LicenseModel>
    {
        return licenseService.findLicense(manager, id, version, licenseUrl!!)
    }

    /*@PostMapping("/vulnerabilities")
    fun getDependencyVulnerabilities(resp: HttpServletResponse,
                                     @PathVariable("manager") manager: String,
                                     @RequestBody artifacts: ArrayList<Artifacts>) : ResponseEntity<Any>
    {
        val vulnerabilies = vulnerabilityService.javaWrapper(artifacts)

        if(vulnerabilies == null) {
            return ResponseEntity(HttpStatus.NO_CONTENT)
        } else {

        }
    }*/

    data class Artifacts(val pm: String, val name: String, val version: String, @JsonIgnore val group: String)

}