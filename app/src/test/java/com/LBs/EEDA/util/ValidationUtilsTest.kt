package com.LBs.EEDA.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

/**
 * Tests unitarios para utilidades de validación
 */
class ValidationUtilsTest {

    @Test
    @DisplayName("Validar nombre válido")
    fun `validate valid name`() {
        val result = ValidationUtils.validateName("Juan")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    @DisplayName("Rechazar nombre vacío")
    fun `reject empty name`() {
        val result = ValidationUtils.validateName("")
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    @DisplayName("Rechazar nombre corto")
    fun `reject short name`() {
        val result = ValidationUtils.validateName("A")
        assertFalse(result.isValid)
    }

    @Test
    @DisplayName("Validar email correcto")
    fun `validate valid email`() {
        val result = ValidationUtils.validateEmail("test@example.com")
        assertTrue(result.isValid)
    }

    @Test
    @DisplayName("Rechazar email inválido")
    fun `reject invalid email`() {
        val result = ValidationUtils.validateEmail("invalid-email")
        assertFalse(result.isValid)
    }

    @Test
    @DisplayName("Validar edad permitida")
    fun `validate allowed age`() {
        val result = ValidationUtils.validateAge(10)
        assertTrue(result.isValid)
    }

    @Test
    @DisplayName("Rechazar edad muy baja")
    fun `reject too young age`() {
        val result = ValidationUtils.validateAge(2)
        assertFalse(result.isValid)
    }

    @Test
    @DisplayName("Rechazar edad muy alta")
    fun `reject too old age`() {
        val result = ValidationUtils.validateAge(150)
        assertFalse(result.isValid)
    }

    @Test
    @DisplayName("Validar PIN correcto")
    fun `validate valid pin`() {
        val result = ValidationUtils.validatePin("1234")
        assertTrue(result.isValid)
    }

    @Test
    @DisplayName("Rechazar PIN corto")
    fun `reject short pin`() {
        val result = ValidationUtils.validatePin("123")
        assertFalse(result.isValid)
    }

    @Test
    @DisplayName("Validar contraseña segura")
    fun `validate strong password`() {
        val result = ValidationUtils.validatePassword("SecurePass123!")
        assertTrue(result.isValid)
    }

    @Test
    @DisplayName("Rechazar contraseña débil")
    fun `reject weak password`() {
        val result = ValidationUtils.validatePassword("123456")
        assertFalse(result.isValid)
    }

    @Test
    @DisplayName("Calcular fortaleza de contraseña")
    fun `calculate password strength`() {
        val weak = ValidationUtils.calculatePasswordStrength("123")
        val medium = ValidationUtils.calculatePasswordStrength("Password1")
        val strong = ValidationUtils.calculatePasswordStrength("SecurePass123!")

        assertTrue(weak < medium)
        assertTrue(medium < strong)
    }

    @Test
    @DisplayName("Truncar texto largo")
    fun `truncate long text`() {
        val text = "Este es un texto muy largo que necesita ser truncado"
        val truncated = ValidationUtils.truncateText(text, 20)

        assertTrue(truncated.length <= 23) // 20 + "..."
        assertTrue(truncated.endsWith("..."))
    }

    @Test
    @DisplayName("Formatear número con separadores")
    fun `format number with separators`() {
        val formatted = ValidationUtils.formatNumber(1234567)
        assertEquals("1,234,567", formatted)
    }

    @Test
    @DisplayName("Formatear número compacto")
    fun `format compact number`() {
        assertEquals("1.2K", ValidationUtils.formatCompactNumber(1234))
        assertEquals("1.5M", ValidationUtils.formatCompactNumber(1_500_000))
    }
}
