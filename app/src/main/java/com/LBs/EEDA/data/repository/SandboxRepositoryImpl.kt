package com.LBs.EEDA.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.LBs.EEDA.domain.model.educational.*
import com.LBs.EEDA.domain.usecase.educational.SandboxRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Implementación simplificada del repositorio de Code Sandbox.
 */
class SandboxRepositoryImpl(
    private val context: Context,
    private val json: Json
) : SandboxRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("sandbox_progress", Context.MODE_PRIVATE)

    override suspend fun getSandbox(id: String): CodeSandbox? {
        return withContext(Dispatchers.IO) {
            getSampleSandboxes().find { it.id == id }
        }
    }

    override suspend fun saveExecution(
        userId: String,
        sandboxId: String,
        code: String,
        result: com.LBs.EEDA.domain.model.educational.ExecutionResult
    ) {
        withContext(Dispatchers.IO) {
            val key = "executions_${userId}_${sandboxId}"
            val currentJson = prefs.getString(key, "[]") ?: "[]"
            val executions = json.decodeFromString<MutableList<ExecutionHistoryEntry>>(currentJson)
            
            executions.add(ExecutionHistoryEntry(
                timestamp = System.currentTimeMillis(),
                userId = userId,
                sandboxId = sandboxId,
                code = code,
                success = result is com.LBs.EEDA.domain.model.educational.ExecutionResult.Success
            ))
            
            if (executions.size > 50) executions.removeAt(0)
            prefs.edit { putString(key, json.encodeToString(executions)) }
            
            // Marcar como completado si fue exitoso
            if (result is com.LBs.EEDA.domain.model.educational.ExecutionResult.Success && result.nextLevelUnlocked) {
                val completedKey = "completed_sandboxes_$userId"
                val completed = prefs.getStringSet(completedKey, mutableSetOf()) ?: mutableSetOf()
                completed.add(sandboxId)
                prefs.edit { putStringSet(completedKey, completed) }
            }
        }
    }

    suspend fun getExecutionHistory(userId: String, sandboxId: String? = null): List<ExecutionHistoryEntry> {
        return withContext(Dispatchers.IO) {
            if (sandboxId != null) {
                val key = "executions_${userId}_${sandboxId}"
                val jsonStr = prefs.getString(key, "[]") ?: "[]"
                json.decodeFromString(jsonStr)
            } else {
                emptyList()
            }
        }
    }

    suspend fun getCompletedSandboxes(userId: String): List<String> {
        return withContext(Dispatchers.IO) {
            val key = "completed_sandboxes_$userId"
            prefs.getStringSet(key, emptySet())?.toList() ?: emptyList()
        }
    }

    private fun getSampleSandboxes(): List<CodeSandbox> {
        return listOf(
            createPythonHelloWorldSandbox(),
            createPasswordGeneratorSandbox(),
            createEncryptionSandbox()
        )
    }

    private fun createPythonHelloWorldSandbox(): CodeSandbox {
        return CodeSandbox(
            id = "sandbox_python_001",
            title = "Tu Primer Programa",
            conceptId = "programming_logic",
            ageBracket = AgeBracket.CHILDHOOD,
            difficulty = ContentDifficulty.BEGINNER,
            language = SandboxLanguage.PYTHON,
            description = "Escribe tu primer programa",
            initialCode = "print(\"Hola\")",
            instructions = "Modifica para decir 'Hola, Mundo!'",
            objectives = listOf(
                CodeObjective(
                    id = "obj_1",
                    description = "Imprimir 'Hola, Mundo!'",
                    validationType = ValidationType.OUTPUT_MATCH,
                    expectedOutput = "Hola, Mundo!"
                )
            ),
            testCases = listOf(
                TestCase(id = "test_1", input = "", expectedOutput = "Hola, Mundo!", description = "Salida esperada")
            ),
            hints = listOf(
                CodeHint(
                    id = "hint_1",
                    triggerCondition = HintTrigger(errorType = CodeErrorType.SYNTAX_ERROR),
                    level = HintLevel.DIRECT,
                    message = "El texto debe estar entre comillas",
                    codeSnippet = "print(\"Hola, Mundo!\")"
                )
            ),
            xpReward = 100,
            unlocksNextLevel = true,
            sandboxConfig = SandboxConfig()
        )
    }

    private fun createPasswordGeneratorSandbox(): CodeSandbox {
        return CodeSandbox(
            id = "sandbox_password_001",
            title = "Generador de Contraseñas",
            conceptId = "password_security",
            ageBracket = AgeBracket.PRE_ADOLESCENT,
            difficulty = ContentDifficulty.INTERMEDIATE,
            language = SandboxLanguage.PYTHON,
            description = "Crea un generador de contraseñas",
            initialCode = "import random\n\ndef generar_password():\n    return \"\"\n\nprint(generar_password())",
            instructions = "Genera contraseña con: 1 mayúscula, 3 minúsculas, 2 números, 1 símbolo",
            objectives = listOf(
                CodeObjective(
                    id = "obj_1",
                    description = "Contraseña de 7 caracteres",
                    validationType = ValidationType.CUSTOM_LOGIC
                )
            ),
            testCases = listOf(TestCase(id = "test_exec", input = "", expectedOutput = "", description = "Ejecución exitosa")),
            hints = listOf(
                CodeHint(
                    id = "hint_1",
                    triggerCondition = HintTrigger(attemptNumber = 1),
                    level = HintLevel.SUBTLE,
                    message = "Usa random.choice() para seleccionar caracteres"
                )
            ),
            xpReward = 200,
            unlocksNextLevel = true,
            sandboxConfig = SandboxConfig(allowImports = listOf("random"))
        )
    }

    private fun createEncryptionSandbox(): CodeSandbox {
        return CodeSandbox(
            id = "sandbox_encrypt_001",
            title = "Cifrado César",
            conceptId = "encryption",
            ageBracket = AgeBracket.PRE_ADOLESCENT,
            difficulty = ContentDifficulty.INTERMEDIATE,
            language = SandboxLanguage.PYTHON,
            description = "Implementa cifrado de César",
            initialCode = "def cifrar(texto, desp):\n    return texto\n\nprint(cifrar(\"HOLA\", 3))",
            instructions = "Cifra 'HOLA' con desplazamiento 3 (resultado: KROD)",
            objectives = listOf(
                CodeObjective(
                    id = "obj_1",
                    description = "Cifrar correctamente",
                    validationType = ValidationType.OUTPUT_MATCH,
                    expectedOutput = "KROD"
                )
            ),
            testCases = listOf(TestCase(id = "test_1", input = "", expectedOutput = "KROD", description = "Cifrado")),
            hints = listOf(
                CodeHint(
                    id = "hint_1",
                    triggerCondition = HintTrigger(errorType = CodeErrorType.WRONG_OUTPUT),
                    level = HintLevel.EXPLANATORY,
                    message = "Usa (numero + desplazamiento) % 26"
                )
            ),
            xpReward = 250,
            unlocksNextLevel = true,
            sandboxConfig = SandboxConfig()
        )
    }
}

@kotlinx.serialization.Serializable
data class ExecutionHistoryEntry(
    val timestamp: Long,
    val userId: String,
    val sandboxId: String,
    val code: String,
    val success: Boolean
)
