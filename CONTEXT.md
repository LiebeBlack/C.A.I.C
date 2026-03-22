# Contexto de la Aplicación "Isla Digital"

## Descripción General
"Isla Digital" es una aplicación móvil desarrollada en Kotlin nativo para Android, utilizando **Jetpack Compose** como framework principal para la UI. La aplicación está diseñada con un enfoque educativo y divertido para niños, centrada en una temática basada en la **Isla de Margarita** (Venezuela).

La app utiliza patrones de diseño modernos (2026), arquitectura MVVM, flujos reactivos (Coroutines + StateFlow) y prácticas actuales de desarrollo en Android.

## Arquitectura y Tecnologías
- **Lenguaje**: Kotlin (versión compatible con Gradle 8.x, compatibilidad Java 17).
- **UI Framework**: Jetpack Compose (Material 3).
- **Arquitectura**: MVVM (Model-View-ViewModel) con Inyección de Dependencias manual sencilla.
- **Estado (State Management)**: `StateFlow` en el ViewModel, recolectado de forma segura en la vista usando `collectAsStateWithLifecycle()`.
- **Almacenamiento Local**: `SharedPreferences` combinado con `kotlinx.serialization` para guardar y estructurar objetos complejos (como el perfil del niño) en un formato JSON persistente.
- **Asincronismo**: Kotlin Coroutines y Kotlin Flow.

## Estructura de Paquetes
Directorio base: `com.liebeblack.isla_digital`

- **`IslaDigitalApp.kt`**: Clase Application base que inicializa el contenedor de Dependencias Manuales (`AppContainer`).
- **`MainActivity.kt`**: Punto de entrada (ComponentActivity). Configura el `Scaffold`, Inyecta el ViewModel mediante `ViewModelProvider.Factory` e inicializa el tema.
- **`domain/`** (Reglas de Negocio / Entidades puras):
  - **`model/ChildProfile.kt`**: Data class inmutable (Data Entity) que almacena la información del niño, su edad, avatar, progreso en juegos, y medallas. Está serializado con `@Serializable`.
  - **`repository/ChildProfileRepository.kt`**: Interface base del repositorio con soporte reactivo (retorna `Flow<Result<T>>`).
- **`data/`** (Capa de Acceso a Datos):
  - **`repository/ChildProfileRepositoryImpl.kt`**: Implementación concreta del repositorio utilizando persistencia JSON basada en `SharedPreferences`.
- **`ui/`** (Capa de Presentación / Compose):
  - **`theme/`**: Configura la paleta de colores de la isla (*IslaColors*), la tipografía y el esquema base `IslaDigitalTheme`.
  - **`components/`**: Componentes visuales reutilizables.
    - **`BigButton.kt`**: Botón 3D gamificado con sombra e interacciones hápticas.
    - **`GlassContainer.kt`**: Contenedor con efecto de "Glassmorphism" y bordes estilizados.
  - **`screens/`**: Pantallas de la app.
    - **`home/HomeScreen.kt`**: Menú principal del niño. Renderiza animaciones fluidas, información de progreso y botones principales de navegación (Jugar, Logros, Álbum).
    - **`home/HomeViewModel.kt`**: Mantiene el estado interactivo (`HomeUiState`) y coordina las llamadas al repositorio usando corrutinas.

## Notas de Desarrollo & Optimizaciones
1. **Evitar aplicar `.blur` indiscriminadamente**: En versiones anteriores, los contenedores "Glass" aplicaban `.blur()` a todo el `Box` causando que el texto / contenido hijo también se difuminara y fuera ilegible. Ahora usamos capas superpuestas donde un `Box` de fondo simula el estilo Glass con opacidad (alpha) y bordes resaltados, manteniendo a los hijos nítidos.
2. **Navegación**: En progreso o simulada (callbacks pasados a nivel de actividad/pantalla).
3. **Manejo de Errores**: Se usa extensamente el patrón \`Result\` (`kotlin.Result`) devuelto a través de Flows y expuesto a estados UI estancos (Sealed Interfaces: `Loading`, `Success`, `Error`).

## Instrucciones para Editar
Cuando vayas a modificar, toma siempre en cuenta que **esta app está dirigida a niños**:
- Mantener colores vibrantes y transiciones amigables.
- Preferir iconografías grandes y texto claro / legible.
- Nunca romper la arquitectura de estado reactiva (StateFlow). Todo cambio visual complejo debería estar encapsulado en `ui/components/`. 
