# BuildLogAI - Android Application

BuildLogAI es una aplicación Android orientada a la gestión y documentación de proyectos de construcción. Permite registrar incidencias, avances y observaciones de obra mediante texto, imágenes y audio, centralizando toda la información del proyecto en una única plataforma.

La aplicación se integra con un backend REST desarrollado en Spring Boot y utiliza servicios de inteligencia artificial para procesar información técnica y generar datos estructurados a partir de los registros creados por los usuarios.

---

## Características principales

### Gestión de usuarios

* Registro de nuevos usuarios.
* Inicio y cierre de sesión seguro.
* Verificación de correo electrónico mediante código de validación enviado por email.
* Gestión de sesiones mediante JWT (JSON Web Tokens).
* Persistencia de sesión entre ejecuciones de la aplicación.

### Gestión de proyectos

* Consulta de proyectos asociados al usuario.
* Búsqueda dinámica de proyectos.
* Navegación detallada por proyecto.
* Acceso centralizado a todos los registros asociados.

### Gestión de registros

* Creación de registros de obra.
* Edición de registros existentes.
* Eliminación de registros.
* Asociación de registros a proyectos.
* Clasificación mediante estados (Abierto / Cerrado).
* Registro de notas y observaciones técnicas.

### Multimedia

* Captura y visualización de imágenes.
* Grabación y reproducción de audio.
* Galería multimedia integrada.
* Visualización de imágenes a pantalla completa.
* Soporte para zoom y navegación gestual.

### Filtrado y organización

* Búsqueda en tiempo real.
* Filtrado de registros por estado.
* Ordenación de registros por fecha.
* Actualización dinámica de resultados.

### Datos estructurados

* Visualización de información procesada automáticamente.
* Presentación de datos técnicos en formato tabular.
* Integración con servicios de IA para extracción y estructuración de información.

### Experiencia de usuario

* Diseño basado en Material Design 3.
* Compatibilidad con modo oscuro.
* Persistencia de preferencias visuales.
* Splash Screen nativa de Android 12+.
* Interfaz optimizada para dispositivos Android modernos.

---

## Arquitectura

BuildLogAI sigue una arquitectura cliente-servidor:

```text
┌─────────────────────┐
│   Android Client    │
│       (Java)        │
└──────────┬──────────┘
           │ REST API
           ▼
┌─────────────────────┐
│ Spring Boot Backend │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ PostgreSQL Database │
└─────────────────────┘
```

La aplicación Android se encarga de la interfaz de usuario, la navegación y el consumo de servicios REST. El backend gestiona la autenticación, autorización, persistencia de datos y procesamiento de la información.

---

## Stack tecnológico

### Desarrollo Android

* Java
* Android SDK
* AndroidX
* Material Design 3
* View Binding

### Networking

* Retrofit 2
* OkHttp
* Gson

### Gestión multimedia

* Glide
* PhotoView
* ViewPager2

### Persistencia local

* SharedPreferences
* JWT Storage

### Servicios externos

* Brevo (envío de correos electrónicos y verificación de cuentas)

---

## Estructura del proyecto

```text
app/
├── activities/
├── adapters/
├── dialogs/
├── model/
├── network/
├── utils/
└── res/
```

### Directorios principales

| Directorio | Descripción                                |
| ---------- | ------------------------------------------ |
| activities | Pantallas y lógica de navegación           |
| adapters   | Adaptadores para RecyclerView              |
| dialogs    | Diálogos personalizados                    |
| model      | DTOs y modelos de datos                    |
| network    | Configuración de Retrofit y servicios REST |
| utils      | Clases auxiliares y utilidades             |
| res        | Recursos gráficos, layouts y cadenas       |

---

## Seguridad

* Autenticación mediante JWT.
* Verificación obligatoria de correo electrónico durante el registro.
* Control de acceso a recursos gestionado desde backend.
* Gestión segura de sesiones de usuario.

---

## Requisitos

* Android Studio Ladybug o superior.
* JDK 17.
* Android SDK 24 o superior.
* Backend BuildLogAI desplegado y accesible.

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/fbrocio/BuildLogAIApp.git
```

### 2. Configurar la URL del backend

Modificar la URL base utilizada por Retrofit:

```java
public static final String BASE_URL = "https://buildlogai-api.onrender.com";
```

### 3. Compilar y ejecutar

1. Abrir el proyecto en Android Studio.
2. Sincronizar dependencias Gradle.
3. Ejecutar la aplicación en un dispositivo físico o emulador Android.

---

## Estado del proyecto

BuildLogAI ha sido desarrollado como Trabajo Fin de Grado (TFG) y constituye una solución orientada a la digitalización de procesos de documentación y seguimiento de proyectos de construcción mediante tecnologías móviles e inteligencia artificial.
