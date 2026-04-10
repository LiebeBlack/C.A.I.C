package com.LBs.EEDA

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.ui.theme.EedaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Actividad de reporte de errores/crash profesional de E.E.D.A.
 * Muestra información técnica detallada y permite copiar reporte en formato Markdown.
 */
class CrashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val crashData = loadCrashDataFromPrefs()

        setContent {
            EedaTheme {
                CrashScreen(
                    crashData = crashData,
                    onRestart = { restartApp() },
                    onCopyReport = { copyReportToClipboard(crashData, format = "text") },
                    onCopyMarkdown = { copyReportToClipboard(crashData, format = "markdown") }
                )
            }
        }
    }

    private fun loadCrashDataFromPrefs(): CrashReportData {
        val prefs = getSharedPreferences("eeda_crash_prefs", Context.MODE_PRIVATE)
        return CrashReportData(
            versionName = prefs.getString("crash_version_name", "Unknown") ?: "Unknown",
            versionCode = prefs.getInt("crash_version_code", 0),
            deviceBrand = prefs.getString("crash_device_brand", "Unknown") ?: "Unknown",
            deviceModel = prefs.getString("crash_device_model", "Unknown") ?: "Unknown",
            deviceManufacturer = prefs.getString("crash_device_manufacturer", "Unknown") ?: "Unknown",
            deviceProduct = prefs.getString("crash_device_product", "Unknown") ?: "Unknown",
            androidVersion = prefs.getString("crash_android_version", "Unknown") ?: "Unknown",
            sdkVersion = prefs.getInt("crash_sdk_version", 0),
            architecture = prefs.getString("crash_architecture", "Unknown") ?: "Unknown",
            supportedAbis = prefs.getString("crash_supported_abis", "Unknown") ?: "Unknown",
            threadName = prefs.getString("crash_thread_name", "Unknown") ?: "Unknown",
            exceptionClass = prefs.getString("crash_exception_class", "Unknown") ?: "Unknown",
            exceptionMessage = prefs.getString("crash_exception", "Sin mensaje") ?: "Sin mensaje",
            stackTrace = prefs.getString("crash_stacktrace", "No disponible") ?: "No disponible",
            cause = prefs.getString("crash_cause", null),
            timestamp = prefs.getLong("crash_timestamp", System.currentTimeMillis())
        )
    }

    private fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun copyReportToClipboard(crashData: CrashReportData, format: String) {
        val report = when (format) {
            "markdown" -> buildMarkdownReport(crashData)
            else -> buildTextReport(crashData)
        }
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("EEDA Crash Report", report)
        clipboard.setPrimaryClip(clip)
        val msg = if (format == "markdown") "Reporte Markdown copiado" else "Reporte copiado"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun buildTextReport(crashData: CrashReportData): String {
        return buildString {
            appendLine("=== EEDA CRASH REPORT ===")
            appendLine("App: ${crashData.versionName} (${crashData.versionCode})")
            appendLine("Device: ${crashData.deviceBrand} ${crashData.deviceModel}")
            appendLine("Manufacturer: ${crashData.deviceManufacturer}")
            appendLine("Product: ${crashData.deviceProduct}")
            appendLine("Android: ${crashData.androidVersion} (API ${crashData.sdkVersion})")
            appendLine("Architecture: ${crashData.architecture}")
            appendLine("ABIs: ${crashData.supportedAbis}")
            appendLine("Thread: ${crashData.threadName}")
            appendLine("Time: ${formatTimestamp(crashData.timestamp)}")
            appendLine()
            appendLine("=== EXCEPTION ===")
            appendLine("Class: ${crashData.exceptionClass}")
            appendLine("Message: ${crashData.exceptionMessage}")
            crashData.cause?.let {
                appendLine()
                appendLine("=== CAUSE ===")
                appendLine(it)
            }
            appendLine()
            appendLine("=== STACK TRACE ===")
            appendLine(crashData.stackTrace)
        }
    }

    private fun buildMarkdownReport(crashData: CrashReportData): String {
        return buildString {
            appendLine("## 🐛 EEDA Crash Report")
            appendLine()
            appendLine("| Campo | Valor |")
            appendLine("|-------|-------|")
            appendLine("| **App Version** | ${crashData.versionName} (${crashData.versionCode}) |")
            appendLine("| **Device** | ${crashData.deviceBrand} ${crashData.deviceModel} |")
            appendLine("| **Manufacturer** | ${crashData.deviceManufacturer} |")
            appendLine("| **Product** | ${crashData.deviceProduct} |")
            appendLine("| **Android** | ${crashData.androidVersion} (API ${crashData.sdkVersion}) |")
            appendLine("| **Architecture** | ${crashData.architecture} |")
            appendLine("| **ABIs** | ${crashData.supportedAbis} |")
            appendLine("| **Thread** | ${crashData.threadName} |")
            appendLine("| **Timestamp** | ${formatTimestamp(crashData.timestamp)} |")
            appendLine()
            appendLine("### ❌ Exception")
            appendLine()
            appendLine("```")
            appendLine("${crashData.exceptionClass}: ${crashData.exceptionMessage}")
            appendLine("```")
            crashData.cause?.let {
                appendLine()
                appendLine("### 🔗 Root Cause")
                appendLine()
                appendLine("```")
                appendLine(it)
                appendLine("```")
            }
            appendLine()
            appendLine("### 📋 Stack Trace")
            appendLine()
            appendLine("```kotlin")
            appendLine(crashData.stackTrace)
            appendLine("```")
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrashScreen(
    crashData: CrashReportData,
    onRestart: () -> Unit,
    onCopyReport: () -> Unit,
    onCopyMarkdown: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.BugReport,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Reporte de Error", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Error Summary Card
            ErrorSummaryCard(crashData)

            Spacer(modifier = Modifier.height(12.dp))

            // Hardware Info Card
            HardwareInfoCard(crashData)

            Spacer(modifier = Modifier.height(12.dp))

            // Throwable Details Card
            ThrowableCard(crashData)

            Spacer(modifier = Modifier.height(12.dp))

            // Stack Trace Card
            StackTraceCard(crashData.stackTrace)

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            ActionButtons(
                onCopyReport = onCopyReport,
                onCopyMarkdown = onCopyMarkdown,
                onRestart = onRestart
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ErrorSummaryCard(crashData: CrashReportData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Excepción Detectada",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${crashData.exceptionClass}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestampDisplay(crashData.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun HardwareInfoCard(crashData: CrashReportData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Memory,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información del Hardware",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Marca", value = crashData.deviceBrand)
            InfoRow(label = "Modelo", value = crashData.deviceModel)
            InfoRow(label = "Fabricante", value = crashData.deviceManufacturer)
            InfoRow(label = "Producto", value = crashData.deviceProduct)
            InfoRow(label = "Android", value = "${crashData.androidVersion} (API ${crashData.sdkVersion})")
            InfoRow(label = "Arquitectura", value = crashData.architecture)
            InfoRow(label = "ABIs", value = crashData.supportedAbis)
        }
    }
}

@Composable
private fun ThrowableCard(crashData: CrashReportData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Code,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFFF6B6B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Throwable Detallado",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0E0E0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Exception Class
            Text(
                text = "Clase:",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
            Text(
                text = crashData.exceptionClass,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFF6B6B)
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Exception Message
            Text(
                text = "Mensaje:",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
            Text(
                text = crashData.exceptionMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFE0E0E0)
                )
            )

            // Cause if present
            crashData.cause?.let { cause ->
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Causa Raíz (Root Cause):",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFFA07A))
                )
                Text(
                    text = cause,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFA07A)
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Hilo: ${crashData.threadName}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray
                )
            )
        }
    }
}

@Composable
private fun StackTraceCard(stackTrace: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 250.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1B1F)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Terminal,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stack Trace",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0E0E0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stackTrace,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFB0B0B0),
                    lineHeight = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onCopyReport: () -> Unit,
    onCopyMarkdown: () -> Unit,
    onRestart: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onCopyMarkdown,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Icon(Icons.Rounded.Description, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Copiar Reporte Markdown (Soporte)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCopyReport,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copiar Texto")
            }

            Button(
                onClick = onRestart,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reiniciar App")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatTimestampDisplay(timestamp: Long): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}
