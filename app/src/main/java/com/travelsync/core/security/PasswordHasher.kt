package com.travelsync.core.security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import android.util.Base64

object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SALT_BYTES = 16

    data class HashResult(val hash: String, val salt: String)

    fun hash(password: String): HashResult {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = deriveKey(password, salt)
        return HashResult(
            hash = Base64.encodeToString(hash, Base64.NO_WRAP),
            salt = Base64.encodeToString(salt, Base64.NO_WRAP)
        )
    }

    fun verify(password: String, saltBase64: String, hashBase64: String): Boolean {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val expected = Base64.decode(hashBase64, Base64.NO_WRAP)
        val actual = deriveKey(password, salt)
        return expected.size == actual.size && expected.indices.all { expected[it] == actual[it] }
    }

    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    }
}
