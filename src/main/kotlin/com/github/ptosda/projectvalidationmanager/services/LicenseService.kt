package com.github.ptosda.projectvalidationmanager.services

import com.github.ptosda.projectvalidationmanager.model.LicenseModel
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Service
class LicenseService {

    val licensesName : List<String> = listOf(
            "Apache Software License, Version 1.1",
            "Apache License, Version 2.0",
            "The 2-Clause BSD License",
            "The 3-Clause BSD License",
            "Creative Commons Legal Code",
            "COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL), Version 1.0",
            "Common Public License - v 1.0",
            "Eclipse Public License - v 1.0",
            "Eclipse Public License - v 2.0",
            "GNU GENERAL PUBLIC LICENSE, Version 1",
            "GNU GENERAL PUBLIC LICENSE, Version 2",
            "GNU GENERAL PUBLIC LICENSE, Version 3",
            "GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1",
            "GNU LESSER GENERAL PUBLIC LICENSE, Version 3",
            "MIT License",
            "Mozilla Public License Version 1.1",
            "Mozilla Public License, Version 2.0",
            "MICROSOFT SOFTWARE LICENSE")

    val licensesRelatedWords : HashMap<String, String> = hashMapOf(
            Pair("http://www.apache.org/licenses/LICENSE-1.1" ,"Apache Software License, Version 1.1"),
            Pair("http://www.apache.org/licenses/LICENSE-2.0" ,"Apache Software License, Version 2.0"),
            Pair("https://opensource.org/licenses/BSD-2-Clause", "The 2-Clause BSD License"),
            Pair("https://opensource.org/licenses/BSD-3-Clause", "The 3-Clause BSD License"),
            Pair("The BSD License", "The 3-Clause BSD License"),
            Pair("http://repository.jboss.org/licenses/cc0-1.0.txt", "Creative Commons Legal Code"),
            Pair("https://www.eclipse.org/legal/epl-v10.html", "Eclipse Public License - v 1.0"),
            Pair("https://www.eclipse.org/legal/cpl-v10.html", "Common Public License - v 1.0"),
            Pair("https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt", "Eclipse Public License - v 2.0"),
            Pair("https://www.gnu.org/licenses/gpl-1.0", "GNU GENERAL PUBLIC LICENSE, Version 1"),
            Pair("https://www.gnu.org/licenses/gpl-2.0", "GNU GENERAL PUBLIC LICENSE, Version 2"),
            Pair("https://www.gnu.org/licenses/gpl-3.0", "GNU GENERAL PUBLIC LICENSE, Version 3"),
            Pair("https://www.gnu.org/licenses/lgpl-2.1", "GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1"),
            Pair("https://www.gnu.org/licenses/lgpl-3.0", "GNU LESSER GENERAL PUBLIC LICENSE, Version 3"),
            Pair("https://opensource.org/licenses/MIT", "MIT License"),
            Pair("https://www.mozilla.org/en-US/MPL/1.1", "Mozilla Public License Version 1.1"),
            Pair("https://www.mozilla.org/en-US/MPL/2.0", "Mozilla Public License, Version 2.0"),
            Pair("Microsoft .NET Library", "MICROSOFT SOFTWARE LICENSE")
    )

    fun findLicense(manager: String, id: String, version: String, licenseUrl: String): ArrayList<LicenseModel> {
        val url = URL(licenseUrl)
        val connection = url.openConnection() as HttpURLConnection
        val licenses = arrayListOf<LicenseModel>()
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "dependency validation server")

        val statusCode = connection.responseCode

        if(statusCode != 200) {
            throw Exception("Error fetching license") //TODO Make custom exception to use with problem+json
        }

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
        licenseName?.let {
            val source = "Found license name or url in $licenseUrl"
            licenses.add(LicenseModel(licenseName, arrayListOf(source)))
        }

        return licenses
    }

    private fun findLicenseUrlInFile(licenseContent: String): String? {
        for (url in licensesRelatedWords.keys)
        {
            if (licenseContent.contains(url))
            {
                return licensesRelatedWords[url]
            }
        }
        return null
    }

    private fun findLicenseNameInFile(licenseContent: String): String? {
        for (name in licensesName)
        {
            if (licenseContent.contains(name))
            {
                return name
            }
        }
        return null
    }


}