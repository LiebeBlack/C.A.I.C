# Manual Técnico - Isla Digital

## Índice
1. [Arquitectura del Sistema](#arquitectura-del-sistema)
2. [Estructura de Carpetas](#estructura-de-carpetas)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Configuración de Desarrollo](#configuración-de-desarrollo)
5. [Guía de Compilación Multiplataforma](#guía-de-compilación-multiplataforma)
6. [Optimización para Dispositivos de Gama Baja](#optimización)
7. [Persistencia de Datos](#persistencia-de-datos)
8. [Sistema de Gamificación](#sistema-de-gamificación)
9. [Integración de Niveles](#integración-de-niveles)

---

## Arquitectura del Sistema

Isla Digital sigue una arquitectura limpia (Clean Architecture) con separación de responsabilidades:

```
lib/
├── core/              # Capa de dominio y datos
│   ├── models/        # Entidades del negocio
│   ├── providers/     # Estado (Riverpod)
│   ├── services/      # Servicios (persistencia)
│   ├── theme/         # Temas y colores
│   └── utils/         # Utilidades
└── ui/                # Capa de presentación
    ├── levels/        # Niveles 1-5
    ├── screens/       # Pantallas principales
    └── widgets/       # Componentes reutilizables
```

### Patrones de Diseño
- **State Management**: Riverpod (reactive, unidirectional data flow)
- **Repository Pattern**: LocalStorageService para persistencia
- **Provider Pattern**: Inyección de dependencias

---

## Estructura de Carpetas

### Core Layer

#### `core/models/`
- `child_profile.dart` - Entidad de perfil del niño
- `badge.dart` - Entidad de insignias y gamificación
- `models.dart` - Barrel file

#### `core/providers/`
- `app_providers.dart` - Providers de Riverpod con persistencia
- `providers.dart` - Barrel file

#### `core/services/`
- `local_storage_service.dart` - Servicio de SharedPreferences
- `services.dart` - Barrel file

#### `core/theme/`
- `app_theme.dart` - Colores margariteños, tipografía Nunito
- `theme.dart` - Barrel file

#### `core/utils/`
- `input_adapter.dart` - Adaptadores para mouse/touch
- `utils.dart` - Barrel file

### UI Layer

#### `ui/levels/`
- `level1/` - Mi Primer Encuentro (encendido, botones, cuidado)
- `level2/` - Conectados (chat, llamadas, fotos)
- `level3/` - Explorador Seguro (detective web)
- `level4/` - Super Tareas (calculadora, calendario, cámara)
- `level5/` - Pequeño Artista (dibujo, música)

#### `ui/screens/`
- `home_screen.dart` - Pantalla principal
- `profile_setup_screen.dart` - Creación de perfil
- `level_select_screen.dart` - Selector de niveles
- `parental_dashboard_screen.dart` - Control parental

#### `ui/widgets/`
- `big_button.dart` - Botones grandes para niños
- `island_background.dart` - Fondo temático de Margarita
- `progress_widgets.dart` - Barras de progreso, indicadores
- `badge_widgets.dart` - Insignias animadas
- `widgets.dart` - Barrel file

---

## Stack Tecnológico

### Framework
- **Flutter** >= 3.0.0 (SDK de Dart)
- **Dart** >= 3.0.0

### Dependencias Principales
```yaml
flutter_riverpod: ^2.4.9      # Gestión de estado
shared_preferences: ^2.2.2    # Persistencia local
google_fonts: ^6.1.0          # Tipografía
audioplayers: ^5.2.1          # Audio
lottie: ^2.7.0                # Animaciones
confetti: ^0.7.0              # Efectos de celebración
```

### Dependencias de Desarrollo
```yaml
flutter_launcher_icons: ^0.13.1   # Iconos multiplataforma
flutter_native_splash: ^2.3.6     # Splash screen
```

---

## Configuración de Desarrollo

### Requisitos Previos
1. Flutter SDK instalado
2. Android Studio / VS Code
3. JDK 11+ (para Android)
4. Visual Studio 2019+ (para Windows)
5. GCC toolchain (para Linux)

### Instalación
```bash
# Clonar repositorio
cd isla_digital

# Instalar dependencias
flutter pub get

# Generar iconos y splash
flutter pub run flutter_launcher_icons:main
flutter pub run flutter_native_splash:create

# Ejecutar en modo debug
flutter run
```

---

## Guía de Compilación Multiplataforma

### Android
```bash
# APK de release
flutter build apk --release

# App Bundle para Play Store
flutter build appbundle --release
```

**Requisitos:**
- minSdkVersion: 21
- targetSdkVersion: 34
- compileSdkVersion: 34

### Windows
```bash
flutter build windows --release
```

**Salida:** `build/windows/x64/runner/Release/`

### Linux
```bash
flutter build linux --release
```

**Salida:** `build/linux/x64/release/bundle/`

### Configuración Multiplataforma en `pubspec.yaml`

```yaml
flutter_launcher_icons:
  android: "launcher_icon"
  image_path: "assets/icons/app_icon.png"
  adaptive_icon_background: "#0066CC"
  windows:
    generate: true
    image_path: "assets/icons/app_icon.png"
  linux:
    generate: true
    image_path: "assets/icons/app_icon.png"
```

---

## Optimización

### Técnicas Implementadas

#### 1. Gestión de Memoria
- Uso de `const` constructors donde sea posible
- Limpieza de controllers en `dispose()`
- Cache de imágenes con dimensiones específicas
- Limitación de items en listas largas

#### 2. Animaciones Eficientes
- `AnimatedContainer` en lugar de setState manual
- `CustomPainter` optimizado para el lienzo de dibujo
- Animaciones con `TickerMode` para pausar cuando no son visibles

#### 3. Reducción de Assets
- Iconos vectoriales (SVG) en lugar de PNGs múltiples
- Fuentes de sistema como fallback
- Compresión de audio

#### 4. Input Adaptativo
- Soporte nativo para mouse y touch
- Detección de plataforma para ajustar comportamiento
- Scroll behavior optimizado para desktop

### Benchmarks de Rendimiento

| Métrica | Objetivo | Implementación |
|---------|----------|----------------|
| Tiempo de inicio | < 3 segundos | Lazy loading de niveles |
| Memoria RAM | < 150 MB | Gestión de estado eficiente |
| Tamaño APK | < 50 MB | Assets optimizados |
| FPS | 60 consistentes | Uso de const, minimal rebuilds |

---

## Persistencia de Datos

### SharedPreferences

La aplicación utiliza `shared_preferences` para almacenamiento local:

#### Datos Almacenados
```dart
String _profileKey = 'child_profile';        // Perfil del niño
String _settingsKey = 'parental_settings'; // Configuración
String _badgesKey = 'earned_badges';         // Insignias
String _playTimeKey = 'total_play_time';     // Tiempo de uso
String _progressKey = 'progress_{levelId}';  // Progreso por nivel
```

#### Modelo de Datos (JSON)
```json
{
  "id": "timestamp",
  "name": "Nombre del niño",
  "age": 5,
  "avatar": "0",
  "createdAt": "2024-01-01T00:00:00.000Z",
  "totalPlayTimeMinutes": 120,
  "currentLevel": 3,
  "levelProgress": {
    "level_1": 100,
    "level_2": 75,
    "level_3": 25
  },
  "earnedBadges": ["primer_encuentro", "maestro_botones"]
}
```

---

## Sistema de Gamificación

### Insignias Margariteñas

| ID | Nombre | Descripción | Puntos |
|----|--------|-------------|--------|
| primer_encuentro | Perla de Sabiduría | Aprende a cuidar tu dispositivo | 100 |
| maestro_botones | Explorador de Botones | Domina botones y gestos | 200 |
| comunicador_seguro | Palometa del Mar | Comunícate de forma segura | 150 |
| llamada_experta | Capitán del Teléfono | Realiza llamadas de práctica | 250 |
| detective_seguro | Detective de la Isla | Identifica sitios seguros | 200 |
| escudo_digital | Escudo del Castillo | Protege tu información | 300 |
| calculador_frutas | Rey del Mango | Suma con frutas tropicales | 200 |
| fotografo_misiones | Ojo de Cotorra | Completa misiones de cámara | 300 |
| artista_isla | Pintor de Atardeceres | Crea obras de arte | 250 |
| musician_tropical | Ritmo de Tambor | Compón música caribeña | 350 |
| super_estrella | Estrella de Margarita | Completa todos los niveles | 1000 |
| explorador_diario | Sol Naciente | Usa la app todos los días | 500 |

### Widgets de Gamificación

#### `AnimatedBadgeWidget`
- Animación de escala elástica
- Efecto de brillo pulsante
- Personalizable por tamaño y color

#### `BadgeRewardDialog`
- Diálogo modal de celebración
- Efectos de confeti
- Botón de continuación

---

## Integración de Niveles

### Estructura de un Nivel

Cada nivel debe seguir esta estructura:

```dart
class LevelXScreen extends ConsumerStatefulWidget {
  // Implementación del nivel
}

class _LevelXScreenState extends ConsumerState<LevelXScreen> {
  int currentStep = 0;
  int totalProgress = 0;
  
  void _completeStep(int points) {
    // Actualizar progreso
    ref.read(levelProgressProvider('level_x').notifier).setProgress(...);
    ref.read(currentProfileProvider.notifier).addProgress('level_x', points);
  }
  
  void _completeLevel() {
    // Otorgar insignia
    ref.read(currentProfileProvider.notifier).addBadge('badge_id');
    ref.read(currentProfileProvider.notifier).unlockLevel(x + 1);
  }
}
```

### Registro en LevelSelectScreen

Agregar import y case en el switch de navegación.

---

## Mantenimiento y Escalabilidad

### Testing
- Tests de widgets para cada nivel
- Tests de integración para flujos completos
- Golden tests para UI consistente

### CI/CD Recomendado
```yaml
# .github/workflows/build.yml
- Análisis estático (flutter analyze)
- Tests automatizados
- Compilación multiplataforma
- Generación de artifacts
```

### Versionado
Seguir [Semantic Versioning](https://semver.org/):
- MAJOR: Cambios incompatibles
- MINOR: Nuevas funcionalidades
- PATCH: Correcciones de bugs

---

## Contacto y Soporte

Para soporte técnico o reportar issues:
- Documentación: `/docs/`
- Issues: Repositorio del proyecto
- Email: soporte@isladigital.ve

---

*Documento generado para el proyecto Isla Digital - Nueva Esparta, Venezuela*
*Versión 1.0.0 - Febrero 2026*
