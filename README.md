# 🎬 Cartelera TMDB - App Android con Jetpack Compose y Retrofit

Aplicación móvil moderna en Android desarrollada con **Kotlin** y **Jetpack Compose** que consume la API REST pública de **The Movie Database (TMDB)**. La app permite explorar películas populares, realizar búsquedas en tiempo real, filtrar por género, ver el detalle de cada película, manejar estados de conexión online/offline y paginar resultados mediante scroll infinito.

---

## 🚀 Características Principales

- **Consumo de API REST con Retrofit:** Integración con 5 endpoints de TMDB con autenticación segura por Bearer Token o API Key.
- **Arquitectura Limpia en Capas (Clean / MVVM):**
  - **UI:** Componentes declarativos en Jetpack Compose (Material 3).
  - **ViewModel:** Manejo reactivo de estado con `StateFlow`, `debounce` de búsqueda y paginación.
  - **Domain:** Modelos de negocio puros (`Movie`, `MovieDetail`, `Genre`) y contratos (`MovieRepository`).
  - **Repository & Remote Data Source:** Mapeo de DTOs con Gson, cliente OkHttp con logging y manejo centralizado de excepciones.
  - **Inyección de Dependencias:** Contenedor de dependencias centralizado (`AppContainer`) desacoplado y seguro.
- **Scroll Infinito (Paginación automática):** Detección reactiva de proximidad al final del `LazyColumn` que carga páginas sucesivas sin bloquear la interfaz.
- **Doble criterio de filtrado / búsqueda con endpoints dedicados:**
  - Búsqueda por título (`/search/movie`) con debounce de 500 ms.
  - Filtro por categoría o género (`/discover/movie`) mediante chips interactivos obtenidos dinámicamente (`/genre/movie/list`).
- **Pantalla de Detalle Completa (`/movie/{id}`):** Vista enriquecida con póster, banner de cabecera, calificación, duración formateada, fecha de estreno, idioma original, sinopsis y chips de géneros.
- **Indicador de Conectividad (Online / Offline):** Monitoreo reactivo de la red mediante `ConnectivityManager.NetworkCallback` con banner animado que avisa la pérdida o restablecimiento de conexión.
- **Manejo Robusto de Errores:** Pantallas dedicadas para estados de carga, resultados vacíos y vista de error con botón **"Reintentar"**.

---

## 📡 Endpoints Consumidos (TMDB API v3)

| # | Endpoint | Método HTTP | Parámetros | Descripción / Función en la App |
|---|---|---|---|---|
| **1** | `/movie/popular` | `GET` | `page` (Int) | Carga la cartelera inicial con paginación infinita. |
| **2** | `/search/movie` | `GET` | `query` (String), `page` (Int) | Búsqueda reactiva de películas por texto. |
| **3** | `/discover/movie` | `GET` | `with_genres` (Int), `page` (Int) | Filtrado de películas por género seleccionado. |
| **4** | `/genre/movie/list` | `GET` | *(ninguno)* | Obtiene el catálogo de géneros para los chips de filtro. |
| **5** | `/movie/{id}` | `GET` | `id` (Path param) | Consulta la información técnica detallada de una película. |

Todas las solicitudes incluyen automáticamente la cabecera `Authorization: Bearer <TOKEN>` (o `api_key`) y el parámetro `language=es-ES` a través del `AuthInterceptor`.

---

## 🛠️ Tecnologías y Librerías

- **Lenguaje:** Kotlin
- **UI Toolkit:** Jetpack Compose con Material Design 3
- **Imágenes:** Coil Compose (carga asíncrona de pósters y banners)
- **Navegación:** Navigation Compose
- **Red:** Retrofit 2 + OkHttp 3 Logging Interceptor + Gson
- **Concurrencia:** Kotlin Coroutines & StateFlow / SharedFlow

---

## ⚙️ Configuración y Ejecución

1. Clona el repositorio:
   ```bash
   git clone <URL_DE_TU_REPOSITORIO>
   ```
2. Obtén un token o API Key en [The Movie Database (TMDB)](https://www.themoviedb.org/settings/api).
3. Abre o edita el archivo `local.properties` en la raíz del proyecto y agrega tu token:
   ```properties
   TMDB_ACCESS_TOKEN=tu_bearer_token_o_api_key_aqui
   ```
4. Abre el proyecto en **Android Studio**, sincroniza Gradle y ejecuta en tu emulador o dispositivo físico.
