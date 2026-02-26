# Assets de Iconos

Esta carpeta contiene los iconos de la aplicación para todas las plataformas.

## Archivos requeridos:

### Android
- `app_icon.png` - Icono principal (1024x1024px recomendado)
- `app_icon_foreground.png` - Capa foreground para adaptive icons (Android 8.0+)
- `app_icon_background.png` - Capa background para adaptive icons

### Windows/Linux
- `app_icon.png` - Icono de la aplicación (256x256px mínimo)

### Splash Screen
- `splash_logo.png` - Logo para pantalla de inicio (centrado)
- `splash_branding.png` - Branding/texto para parte inferior

## Especificaciones:

### Icono Principal
- Resolución: 1024x1024px
- Formato: PNG con transparencia
- Contenido: Logo de Isla Digital, elementos tropicales
- Colores: Azul océano (#0066CC), amarillo sol (#FFD93D), verde palmera (#4CAF50)

### Splash Screen
- Fondo: Color arena claro (#FDF6E3)
- Logo centrado: 200x200px mínimo
- Branding inferior: ancho máximo 600px

## Generación automática:
Después de colocar los archivos, ejecutar:
```bash
flutter pub run flutter_launcher_icons:main
flutter pub run flutter_native_splash:create
```
