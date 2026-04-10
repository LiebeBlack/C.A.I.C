package com.LBs.EEDA.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Utilidades de seguridad para E.E.D.A
 * Manejo de encriptación, hashing y almacenamiento seguro
 */
object SecurityUtils {

    private const val AES_KEY_SIZE = 256
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12
    private const val PREFS_NAME = "eeda_secure_prefs"

    /**
     * Genera hash SHA-256 de un string
     */
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Genera hash SHA-512 de un string (más seguro)
     */
    fun sha512(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-512").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Genera un salt aleatorio
     */
    fun generateSalt(length: Int = 16): String {
        val random = SecureRandom()
        val salt = ByteArray(length)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    /**
     * Hashea contraseña con salt
     */
    fun hashPassword(password: String, salt: String): String {
        return sha512(password + salt)
    }

    /**
     * Verifica contraseña contra hash almacenado
     */
    fun verifyPassword(password: String, salt: String, storedHash: String): Boolean {
        return hashPassword(password, salt) == storedHash
    }

    /**
     * Encripta datos usando AES-GCM
     */
    fun encrypt(plaintext: String, key: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = SecretKeySpec(key.toByteArray().copyOf(32), "AES")

        // Generar IV aleatorio
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        val ciphertext = cipher.doFinal(plaintext.toByteArray())

        // Combinar IV + ciphertext y codificar en Base64
        val combined = iv + ciphertext
        return Base64.getEncoder().encodeToString(combined)
    }

    /**
     * Desencripta datos usando AES-GCM
     */
    fun decrypt(ciphertext: String, key: String): String {
        val combined = Base64.getDecoder().decode(ciphertext)

        // Separar IV y ciphertext
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = combined.copyOfRange(GCM_IV_LENGTH, combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = SecretKeySpec(key.toByteArray().copyOf(32), "AES")

        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return String(cipher.doFinal(encrypted))
    }

    /**
     * Genera token de sesión seguro
     */
    fun generateSessionToken(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    /**
     * Almacena datos sensibles de forma segura
     */
    fun saveSecureData(context: Context, key: String, value: String, encryptionKey: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encrypted = encrypt(value, encryptionKey)
        prefs.edit { putString(key, encrypted) }
    }

    /**
     * Recupera datos sensibles almacenados
     */
    fun getSecureData(context: Context, key: String, encryptionKey: String): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encrypted = prefs.getString(key, null) ?: return null
        return try {
            decrypt(encrypted, encryptionKey)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Elimina datos sensibles
     */
    fun removeSecureData(context: Context, key: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { remove(key) }
    }

    /**
     * Genera PIN aleatorio seguro
     */
    fun generateSecurePin(length: Int = 4): String {
        val random = SecureRandom()
        val pin = StringBuilder()
        repeat(length) {
            pin.append(random.nextInt(10))
        }
        return pin.toString()
    }

    /**
     * Valida código de acceso de un solo uso (OTP)
     */
    fun validateOtp(storedOtp: String, inputOtp: String, timestamp: Long, validityMinutes: Int = 10): Boolean {
        val now = System.currentTimeMillis()
        val validityMillis = validityMinutes * 60 * 1000

        return inputOtp == storedOtp && (now - timestamp) < validityMillis
    }

    /**
     * Genera código de recuperación
     */
    fun generateRecoveryCode(): String {
        val random = SecureRandom()
        val code = StringBuilder()
        repeat(4) {
            if (code.isNotEmpty()) code.append("-")
            repeat(4) {
                code.append(String.format("%04X", random.nextInt(65536)))
            }
        }
        return code.toString()
    }

    /**
     * Sanitiza y escapa texto para prevenir XSS
     */
    fun sanitizeForDisplay(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;")
    }

    /**
     * Detecta si el dispositivo está rooteado (básico)
     */
    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { java.io.File(it).exists() }
    }

    /**
     * Rate limiting simple
     */
    class RateLimiter(private val maxAttempts: Int, private val windowMillis: Long) {
        private val attempts = mutableMapOf<String, MutableList<Long>>()

        fun isAllowed(key: String): Boolean {
            val now = System.currentTimeMillis()
            val windowStart = now - windowMillis

            // Limpiar intentos antiguos
            attempts[key]?.removeAll { it < windowStart }

            // Verificar límite
            val currentAttempts = attempts[key]?.size ?: 0
            return currentAttempts < maxAttempts
        }

        fun recordAttempt(key: String) {
            attempts.getOrPut(key) { mutableListOf() }.add(System.currentTimeMillis())
        }

        fun getRemainingAttempts(key: String): Int {
            val now = System.currentTimeMillis()
            val windowStart = now - windowMillis
            attempts[key]?.removeAll { it < windowStart }
            return maxAttempts - (attempts[key]?.size ?: 0)
        }

        fun reset(key: String) {
            attempts.remove(key)
        }
    }

    /**
     * Bloqueo temporal tras intentos fallidos
     */
    class AccountLockout(private val maxFailedAttempts: Int, private val lockoutMinutes: Int) {
        private val failedAttempts = mutableMapOf<String, Pair<Int, Long>>()

        fun isLocked(accountId: String): Boolean {
            val (attempts, lockTime) = failedAttempts[accountId] ?: return false
            if (attempts < maxFailedAttempts) return false

            val lockoutMillis = lockoutMinutes * 60 * 1000
            return System.currentTimeMillis() - lockTime < lockoutMillis
        }

        fun recordFailedAttempt(accountId: String) {
            val (attempts, _) = failedAttempts[accountId] ?: Pair(0, 0)
            failedAttempts[accountId] = Pair(attempts + 1, System.currentTimeMillis())
        }

        fun recordSuccessfulAttempt(accountId: String) {
            failedAttempts.remove(accountId)
        }

        fun getRemainingLockoutTime(accountId: String): Long {
            val (_, lockTime) = failedAttempts[accountId] ?: return 0
            val lockoutMillis = lockoutMinutes * 60 * 1000
            val remaining = lockoutMillis - (System.currentTimeMillis() - lockTime)
            return remaining.coerceAtLeast(0)
        }
    }
}
