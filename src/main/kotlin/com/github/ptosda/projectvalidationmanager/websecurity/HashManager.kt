package com.github.ptosda.projectvalidationmanager.websecurity

import java.security.MessageDigest

class HashManager(
        private val md: MessageDigest = MessageDigest.getInstance("SHA1")
) {
    fun hashToHex(buffer: ByteArray) : String {
        md.reset()
        md.update(buffer)
        val digest = md.digest()

        var result = ""

        digest.forEach {
            result += String.format("%x", it)
        }

        return result
    }
}