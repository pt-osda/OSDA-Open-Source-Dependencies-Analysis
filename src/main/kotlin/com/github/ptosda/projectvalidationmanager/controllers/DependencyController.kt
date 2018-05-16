package com.github.ptosda.projectvalidationmanager.controllers

import com.github.ptosda.projectvalidationmanager.model.LicenseModel
import com.github.ptosda.projectvalidationmanager.services.LicenseService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/{manager}/dependency/{id}/{version}")
class DependencyController(val licenseService: LicenseService) {

    //TODO Need to do exception handling. Need to check licenseUrl
    @GetMapping("/licenses", produces = ["application/json"])
    fun getDependencyLicenses(@PathVariable("manager") manager: String,
                              @PathVariable("id") id: String,
                              @PathVariable("version") version: String,
                              @RequestParam(value="githubProject", required = false) githubProject: String?,
                              @RequestParam(value="licenseUrl", required = false) licenseUrl: String?): List<LicenseModel>
    {
        return licenseService.findLicense(manager, id, version, licenseUrl!!)
    }

    @GetMapping("/vulnerabilities")
    fun getDependencyVulnerabilities(@PathVariable("manager") manager: String,
                                     @PathVariable("id") id: String,
                                     @PathVariable("version") version: String)
    {

    }

}