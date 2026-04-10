# E.E.D.A. - Registro de Assets (Virtual Asset Registry)

Este documento funciona como la base de datos técnica y de planificación para los recursos gráficos, lúdicos y de sonido del proyecto **E.E.D.A.**. 
La arquitectura `VirtualAssetManager` está diseñada para consultar este registro lógicamente u operar bajo demanda, manteniendo una tolerancia a fallos del 100%.

| Nombre del Recurso | Ruta Relativa | Tipo de Archivo | Estado |
| :--- | :--- | :---: | :---: |
| Intro Animada (Fase Sensorial) | `intro_sensorial.mp4` | Video | 🟡 Pendiente |
| Textura de Arena Margarita | `textures/arena_margarita.webp` | Imagen (WebP) | 🟢 Listo |
| Modelo 3D Perla Interactiva | `models/perla_digital.gltf` | Modelo 3D | 🟡 Pendiente |
| BGM Atardecer (Loop) | `audio/bgm_atardecer.ogg` | Audio | 🟡 Pendiente |
| Sprite Ciber-Escudo | `sprites/escudo_defensa.png` | Imagen | 🟡 Pendiente |
| Configuración de Niveles | `config/levels_cpa.json` | JSON | 🟢 Listo |
| Certificados Vectoriales | `certs/cert_base.svg` | Vector | 🟡 Pendiente |

*Nota para el desarrollador:* Cuando los recursos con estado "🟡 Pendiente" se agreguen a la respectiva ruta dentro del directorio `/src/main/assets/`, el sistema los inyectará automáticamente en el hilo de ejecución sin necesidad de recompilar la lógica de enrutamiento.
