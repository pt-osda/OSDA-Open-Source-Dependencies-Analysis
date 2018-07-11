package com.github.ptosda.projectvalidationmanager

import java.security.MessageDigest

class HashManager(private val md: MessageDigest = MessageDigest.getInstance("SHA1")) {

    fun hashIt(buffer: ByteArray) : String {
        md.reset()
        md.update(buffer)
        val digest = md.digest()

        val result = StringBuilder(digest.size * 2)

        val HEX_CHARS = "0123456789ABCDEF"
        digest.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }
}