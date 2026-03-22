# 🏝️ Assets for Isla Digital

I successfully generated high-quality, premium image assets for the **Isla Digital** app. These designs follow a modern, professional, and slightly futuristic theme that aligns with the "Digital Island" name.

## 📁 Generated Assets

````carousel
![Icono de la App](file:///C:/Users/Liebe/.gemini/antigravity/brain/cad3aa62-afdd-4bc0-8031-150f71667401/app_icon_isla_digital_1774159216297.png)
<!-- slide -->
![Pantalla de Inicio (Splash)](file:///C:/Users/Liebe/.gemini/antigravity/brain/cad3aa62-afdd-4bc0-8031-150f71667401/splash_screen_isla_digital_1774159231648.png)
<!-- slide -->
![Ilustración Principal](file:///C:/Users/Liebe/.gemini/antigravity/brain/cad3aa62-afdd-4bc0-8031-150f71667401/isla_digital_main_illustration_1774159253139.png)
````

## 🛠️ Cómo usar estos archivos en tu proyecto

Para implementar estos recursos en tu proyecto de **Kotlin/Android Studio**, puedes seguir estos pasos:

### 1. Icono de la App (`ic_launcher`)
El archivo `app_icon_isla_digital.png` está diseñado para ser el icono principal. 
- Recomendado: Usa el **Image Asset Studio** en Android Studio (`Right Click res > New > Image Asset`) y selecciona este archivo para generar automáticamente todas las densidades necesarias (`mdpi`, `hdpi`, `xhdpi`, etc.).

### 2. Pantalla de Bienvenida (Splash Screen)
El archivo `splash_screen_isla_digital.png` es ideal para el fondo de tu actividad de Splash.
- Guárdalo en `app/src/main/res/drawable/splash_background.png`.
- Luego, en tu tema de Splash, usa: `android:windowBackground="@drawable/splash_background"`.

### 3. Ilustración del Home
El archivo `isla_digital_main_illustration.png` es perfecto para la pantalla principal (`HomeScreen.kt`).
- Colócalo en `app/src/main/res/drawable/isla_home_bg.png`.
- En Jetpack Compose, úsalo así:
  ```kotlin
  Image(
      painter = painterResource(id = R.drawable.isla_home_bg),
      contentDescription = "Fondo Isla Digital",
      modifier = Modifier.fillMaxWidth()
  )
  ```

> [!TIP]
> Estos archivos se guardaron en la carpeta de artefactos de tu sesión para que los puedas descargar o mover a tu proyecto.
