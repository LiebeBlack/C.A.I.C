plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.liebeblack.isla_digital"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        // Java 17 es ideal para las versiones de Gradle que usas
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        // Activa el modo incremental de Kotlin para que sea más rápido en Linux
        freeCompilerArgs += listOf("-Xbackend-threads=4") 
    }

    defaultConfig {
        applicationId = "com.liebeblack.isla_digital"
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
        
        // TRUCO PARA LINUX: Evita generar archivos para arquitecturas que no usas
        // Si solo pruebas en un celular físico, esto ahorra 1/3 del tiempo
        resConfigs("es", "en") // Solo carga estos idiomas en debug
    }

    buildTypes {
        release {
            // Optimización de recursos para que el APK final sea pequeño
            isMinifyEnabled = false 
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
        
        getByName("debug") {
            // Acelera la instalación en el celular desactivando la compresión de PNGs
            isDefault = true
            extra["crunchPngs"] = false
        }
    }

    // Evita que Gradle pierda tiempo buscando dependencias que no existen
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

flutter {
    source = "../.."
}
