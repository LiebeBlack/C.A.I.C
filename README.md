# E.E.D.A. (Ecosistema Educativo Digital Adaptable)

![Android Badge](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![Kotlin Badge](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white) ![Compose Badge](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white) ![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge)

**E.E.D.A.** es una plataforma pedagógica de vanguardia y una aplicación móvil nativa para Android, diseñada bajo un enfoque de **aprendizaje adaptativo** y alto impacto visual. Ambientada en la **Isla de Margarita (Venezuela)**, transforma la enseñanza técnica en una experiencia inmersiva, fortaleciendo la lógica y la ciberseguridad en usuarios de 3 a 16 años.

---

## Filosofía Visual & UX

El ecosistema prioriza una estética **Premium Dark** con interfaces de última generación:

* **Glassmorphism & Neomorfismo:** Capas translúcidas con desenfoque de fondo controlado para una profundidad visual superior.
* **Feedback Háptico Avanzado:** Respuesta física inmediata en cada interacción, emulando la sensación de dispositivos de alta gama.
* **Micro-animaciones:** Transiciones fluidas en Jetpack Compose que guían el foco del usuario sin generar carga cognitiva.



---

## Características Principales

* **Modelo Pedagógico CPA:** Metodología Concreto-Pictórico-Abstracto para desglosar conceptos complejos de ciberseguridad.
* **Seguridad por Diseño:** Implementación de prácticas de "debloating" y optimización de recursos para un rendimiento térmico eficiente.
* **Perfil de Aventurero:** Dashboard interactivo con métricas de progreso en Lógica, Creatividad y Resolución de Problemas.
* **Reactividad Total:** Arquitectura basada en flujos inmutables para una UI siempre sincronizada y libre de bloqueos.

---

## Stack Tecnológico

E.E.D.A. está construido con los estándares industriales de 2026:

| Componente | Tecnología |
| :--- | :--- |
| **Lenguaje** | Kotlin 1.9+ (JDK 17) |
| **Framework UI** | Jetpack Compose (Material Design 3) |
| **Arquitectura** | Clean Architecture + MVVM |
| **Concurrencia** | Kotlin Coroutines & StateFlow |
| **Persistencia** | kotlinx.serialization (JSON Local) |

### Estructura de Directorios (Modular)
```text
com.liebeblack.eeda/
 ├── EedaApp.kt              # Inyección de dependencias manual y Context
 ├── MainActivity.kt         # Punto de entrada y Compose Theme Bridge
 ├── domain/                 # Casos de uso y Reglas de negocio (Puro)
 │   └── model/              # Data Classes inmutables
 ├── data/                   # Repositorios y fuentes de datos
 │   └── local/              # Persistencia JSON y SharedPreferences
 └── ui/                     # UI, ViewModels y Estados
     ├── theme/              # GlassTheme, Paleta Neon y Tipografía
     ├── components/         # BigButton, GlassCard, CustomIcons
     └── screens/            # Módulos: Home, Adventure, Profile
