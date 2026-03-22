# Isla Digital

![Android Badge](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![Kotlin Badge](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white) ![Compose Badge](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)

**Isla Digital** es una aplicación móvil nativa para Android diseñada con un enfoque educativo, gamificado y divertido para niños. Ubicada temáticamente en la hermosa *Isla de Margarita (Venezuela)*, la app busca inspirar desarrollo cognitivo (lógica, creatividad y resolución de problemas) mientras los niños exploran y se divierten.

---

## Características Principales

- **Diseño Gamificado y Responsivo:** Implementado íntegramente en Jetpack Compose con animaciones fluidas, diseño 3D interactivo (*Neomorfismo y Glassmorphism*).
- **Gestión de Perfil de Aventurero:** Cada usuario tiene su avatar, registro de medallas y progresión en habilidades (Lógica, Creatividad y Resolución de Problemas).
- **Reactividad Completa:** Arquitectura MVVM usando *Kotlin Coroutines* y *StateFlow* para estado inmutable y predecible.
- **Persistencia Segura (JSON Local):** Almacenamiento rápido y eficiente con `SharedPreferences` y `kotlinx.serialization`.

## Arquitectura y Tecnologías

El proyecto sigue estándares modernos (2026) y patrones de diseño limpios:

### Stack Tecnológico
* **Lenguaje:** Kotlin 1.9+ (Reflejando compatibilidad con Java 17).
* **UI Framework:** Jetpack Compose (Material Design 3).
* **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture simplificada.
* **Manejo de Estados:** `StateFlow` recolectados con `collectAsStateWithLifecycle`.
* **Inyección de Dependencias:** DI Manual e Inicializadores eficientes por `ViewModelProvider.Factory`.
* **Serialización:** `kotlinx.serialization` (Multi-propósito y eficiente para Data Classes inmutables).

### Estructura de Directorios (Capa a Capa)
```text
com.liebeblack.isla_digital/
 ├── IslaDigitalApp.kt      # Application y manual DI container
 ├── MainActivity.kt        # Entry point y Setup del Theme Compose
 ├── domain/                # Capa de Reglas de Negocio
 │    ├── model/ChildProfile.kt 
 │    └── repository/ChildProfileRepository.kt
 ├── data/                  # Capa de Acceso de Datos (Persistencia JSON y Storage)
 │    └── repository/ChildProfileRepositoryImpl.kt
 └── ui/                    # Capa de Interfaz y UI (Compose Views & States)
      ├── theme/            # Color.kt, Type.kt, Theme.kt (Paleta Isla Digital)
      ├── components/       # BigButton.kt, GlassContainer.kt (UI Componentes)
      └── screens/home/     # HomeScreen.kt, HomeViewModel.kt
```

## UI / UX (Orientada a Niños)

La experiencia de desarrollo priorizó las siguientes áreas para los más pequeños:
* **Vibración y Colores Vivos:** Uso de iconos gigantesco y texturas estilo *Glassmorphism* que no interfieren con la legibilidad y ofrecen un resalte 3D.
* **Feedback Háptico:** Todos los botones de acción (`BigButton`) implementan retroalimentación sensible real, animaciones personalizadas en estados flotantes y "pressed", asemejándose a consolas modernas.
* **Flujo Fluido:** Estados estancos de la UI (`Loading`, `Success`, `Error`). Interfaces inmutables que no bloquean nunca el hilo principal de renderizado de UI.

## Pre-requisitos e Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/isla-digital.git
   ```
2. Ábrelo en **Android Studio** (Koala o superior recomendado).
3. Asegúrate de tener al menos **JDK 17** habilitado para sincronizar los DSL de Gradle.
4. Conecta un dispositivo físico o emulador (Min SDK 26 - Oreo+).
5. Sincroniza Gradle (`Sync Project with Gradle Files`) y ejecuta (`Run 'app'`).

> Puedes usar el archivo provisto `.bat` en la raíz si estás configurando Android nativamente sin entorno o de forma desasistida (`preparar_y_compilar.bat`).

## Prácticas y Contribución

- No aplicar `.blur()` en todo el árbol de Compose; mantener el texto totalmente nítido bajo esquemas de contenedor.
- Usar el estándar `Result<T>` en repositorios.
- Todos los estados en el ViewModel y la UI deben estar enmarcados en Sealed Interfaces.
- ¡Mantener el espíritu divertido! Introduce animaciones sutiles y efectos de sonido siempre que aplique.
