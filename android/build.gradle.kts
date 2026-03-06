allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// Bloque de directorios: Lo mantenemos pero simplificado
// Flutter usa esto para que los archivos temporales no ensucien tu proyecto.
val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}

// ¡IMPORTANTE!: He quitado el evaluationDependsOn(":app")
// Esto permite que tus 4 núcleos de Linux trabajen en paralelo de verdad.

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xlint:-options")
        
        // Mejora para Linux: Usa múltiples procesos para compilar Java
        options.isIncremental = true
        options.isFork = true 
    }
}
