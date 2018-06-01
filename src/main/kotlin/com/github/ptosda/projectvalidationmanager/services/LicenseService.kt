package com.github.ptosda.projectvalidationmanager.services

import com.github.ptosda.projectvalidationmanager.model.LicenseModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.log

@Service
class LicenseService {
    val logger : Logger = LoggerFactory.getLogger(LicenseService::class.java)
    val licensesName : List<String> = listOf(
            "Apache-1.1",
            "Apache-2.0",
            "BSD-2-Clause",
            "BSD-3-Clause",
            "CC0-1.0",
            "CPL-1.0",
            "EPL-1.0",
            "EPL-2.0",
            "GPL-1.0",
            "GPL-2.0",
            "GPL-3.0",
            "LGPL-2.1",
            "LGPL-3.0",
            "MIT",
            "MPL-1.1",
            "MPL-2.0",
            "MICROSOFT SOFTWARE LICENSE"
    )

    val licensesRelatedWords : HashMap<String, String> = hashMapOf(
            Pair("http://www.apache.org/licenses/LICENSE-1.1" , "Apache-1.1"),
            Pair("http://www.apache.org/licenses/LICENSE-2.0" , "Apache-2.0"),
            Pair("https://opensource.org/licenses/BSD-2-Clause", "BSD-2-Clause"),
            Pair("https://opensource.org/licenses/BSD-3-Clause", "BSD-3-Clause"),
            Pair("The BSD License", "BSD-3-Clause"),
            Pair("http://repository.jboss.org/licenses/cc0-1.0.txt", "CC0-1.0"),
            Pair("https://www.eclipse.org/legal/epl-v10.html", "EPL-1.0"),
            Pair("https://www.eclipse.org/legal/cpl-v10.html", "CPL-1.0"),
            Pair("https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt", "EPL-2.0"),
            Pair("https://www.gnu.org/licenses/gpl-1.0", "GPL-1.0"),
            Pair("https://www.gnu.org/licenses/gpl-2.0", "GPL-2.0"),
            Pair("https://www.gnu.org/licenses/gpl-3.0", "GPL-3.0"),
            Pair("https://www.gnu.org/licenses/lgpl-2.1", "LGPL-2.1"),
            Pair("https://www.gnu.org/licenses/lgpl-3.0", "LGPL-3.0"),
            Pair("https://opensource.org/licenses/MIT", "MIT"),
            Pair("https://www.mozilla.org/en-US/MPL/1.1", "MPL-1.1"),
            Pair("https://www.mozilla.org/en-US/MPL/2.0", "MPL-2.0"),
            Pair("Microsoft .NET Library", "MICROSOFT SOFTWARE LICENSE"),
            Pair("Apache Software License, Version 1.1", "Apache-1.1"),
            Pair("Apache License, Version 2.0", "Apache-2.0"),
            Pair("The 2-Clause BSD License", "BSD-2-Clause"),
            Pair("The 3-Clause BSD License", "BSD-3-Clause"),
            Pair("Creative Commons Legal Code", "CC0-1.0"),
            Pair("Common Public License - v 1.0", "CPL-1.0"),
            Pair("Eclipse Public License - v 1.0", "EPL-1.0"),
            Pair("Eclipse Public License - v 2.0", "EPL-2.0"),
            Pair("GNU GENERAL PUBLIC LICENSE, Version 1", "GPL-1.0"),
            Pair("GNU GENERAL PUBLIC LICENSE, Version 2", "GPL-2.0"),
            Pair("GNU GENERAL PUBLIC LICENSE, Version 3", "GPL-3.0"),
            Pair("GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1", "LGPL-2.1"),
            Pair("GNU LESSER GENERAL PUBLIC LICENSE, Version 3", "LGPL-3.0"),
            Pair("MIT License", "MIT"),
            Pair("Mozilla Public License Version 1.1", "MPL-1.1"),
            Pair("Mozilla Public License, Version 2.0", "MPL-2.0")
    )

    /**
     * Will attempt to search for the license of dependencies through an url.
     * @param id The name of the dependency to search for its license.
     * @param version The version of the dependency that will have its license searched for.
     * @param licenseUrl The url where the license will be searched for.
     * @return All licenses found for the dependency
     */
    fun findLicense(id: String, version: String, licenseUrl: String): ArrayList<LicenseModel> {
        logger.info("The license information of {} will be searched for.", id)

        val url = URL(licenseUrl)
        val connection = url.openConnection() as HttpURLConnection
        val licenses = arrayListOf<LicenseModel>()
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "dependency validation server")

        val statusCode = connection.responseCode
        if(statusCode != 200) {
            logger.warn("The licenses for the dependency was not found.")
            throw Exception("Error fetching licenses") //TODO Make custom exception to use with problem+json
        }

        logger.info("The licenses of the dependency were found.")
        val input = BufferedReader(InputStreamReader(connection.inputStream))
        val content = StringBuffer()
        while (true) {
            val inputLine = input.readLine() ?: break
            content.append(inputLine)
        }
        input.close()
        connection.disconnect()

        val licenseName = findLicenseNameInFile(content.toString()) ?:
        findLicenseUrlInFile(content.toString())
        licenseName?.let{
            licenses.add(LicenseModel(licenseName, "Found license in $licenseUrl"))
        }

        logger.info("The license information found was successfully obtained and its information prepared for a response.")
        return licenses
    }

    /**
     * Attempts to find in the file a url that identifies the license.
     * @param licenseContent The content of the file to search for.
     * @return A license found through the url or null if one was not found
     */
    private fun findLicenseUrlInFile(licenseContent: String): String? {
        for (url in licensesRelatedWords.keys) {
            if (licenseContent.contains(url, true)) {
                logger.info("A url that identifies the license was successfully found.")
                return licensesRelatedWords[url]
            }
        }
        logger.info("The file did not contained a url that identified the license.")
        return null
    }

    /**
     * Attempts to find in the file a word that identifies the license.
     * @param licenseContent The content of the file to search for.
     * @return A license found through a word or null if one was not found
     */
    private fun findLicenseNameInFile(licenseContent: String): String? {
        for (name in licensesName) {
            if (licenseContent.contains(name)) {
                logger.info("A word that identifies the license was successfully found.")
                return name
            }
        }
        logger.info("The file did not contained a word that identified the license.")
        return null
    }
}