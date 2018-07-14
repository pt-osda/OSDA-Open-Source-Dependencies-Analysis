package com.github.ptosda.projectvalidationmanager.websecurity

import java.security.MessageDigest

class HashManager(
        private val md: MessageDigest = MessageDigest.getInstance("SHA1")
) {
    /**
     * Makes the hash of a byte array
     * @param buffer value to be transformed by hash function
     * @return string with the result of the hash
     */
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