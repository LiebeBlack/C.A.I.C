# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Kotlinx Serialization
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
    @kotlinx.serialization.Serializable <fields>;
}
-keep @kotlinx.serialization.Serializable class *
-dontwarn kotlinx.serialization.**

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep model classes for serialization
-keep class com.LBs.EEDA.domain.model.** { *; }
-keepclassmembers class com.LBs.EEDA.domain.model.** { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile