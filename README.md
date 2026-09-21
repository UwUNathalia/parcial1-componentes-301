# NotaViva — Parcial 01

Aplicación móvil Android para que un periodista administre las entrevistas que ha
realizado sobre casos criminales: registrar casos, consultarlos, editarlos,
buscarlos, guardar entrevistas con sus hallazgos, adjuntar evidencias y cerrar el
caso con una conclusión.

**Universidad Distrital Francisco José de Caldas**
Programación por Componentes — Semestre 2026-3 — Parcial 01
Profesor: Jorge Eduardo Hernández R.

## Integrantes

<!-- TODO: completar antes de la entrega -->
- Nombre y código
- Nombre y código
- Nombre y código

## Tecnologías

| Herramienta | Versión | Para qué |
|---|---|---|
| Kotlin | 2.2.10 | Lenguaje de desarrollo |
| Jetpack Compose | BOM 2026.02.01 | Interfaz declarativa |
| SQLite | API de Android | Persistencia local |
| Navigation Compose | 2.10.1 | Navegación entre pantallas |
| ViewModel + StateFlow | Lifecycle 2.11.0 | Capa de lógica (MVVM) |
| Gradle | 9.5.0 (AGP 9.3.2) | Construcción del proyecto |

- `minSdk` 26 · `targetSdk` 37 · sin servicios web, Firebase ni backend.

> Se usa `minSdk` 26 (Android 8.0) porque es la primera versión que incluye
> `java.time` de forma nativa. Manejar las fechas con la API moderna evita
> `Calendar`/`SimpleDateFormat` y no obliga a activar *core library desugaring*.

### Por qué SQLite y no Room

El enunciado permite SQLite, Room o SharedPreferences. Se escogió
`SQLiteOpenHelper` directamente por tres razones:

1. **Sin procesamiento de anotaciones.** Room genera su código con KSP, que hoy
   no es compatible con el *built-in Kotlin* de Android Gradle Plugin 9
   ([google/ksp#2615](https://github.com/google/ksp/issues/2615)). Usar SQLite
   evita atar el proyecto a esa incompatibilidad.
2. **El SQL queda a la vista.** El esquema y las consultas están escritos en el
   proyecto, lo que hace verificable la capa de persistencia en vez de
   esconderla detrás de anotaciones.
3. **Sin dependencias extra.** `android.database.sqlite` viene en el framework.

La separación de capas se mantiene igual: los DAOs concentran el SQL y el
repositorio es la única puerta de entrada a los datos, de modo que cambiar la
fuente de datos no obligaría a tocar los ViewModels.

## Arquitectura

Se usa **MVVM** con tres capas separadas, tal como pide el enunciado
("se recomienda separar interfaz, lógica y persistencia"):

```
 UI (Compose)  ──eventos──▶  ViewModel  ──llamadas──▶  Repositorio ──▶ SQLite
      ▲                          │                          │
      └──────── StateFlow ───────┘◀────────  Flow  ─────────┘
```

- **Vista (`ui/`)**: solo dibuja y reporta eventos. No conoce la base de datos.
- **ViewModel (`viewmodel/`)**: mantiene el estado de la pantalla y aplica las
  reglas de negocio. No conoce Compose.
- **Modelo (`domain/model`, `data/`)**: entidades y acceso a datos. El
  repositorio es la única puerta hacia la base de datos.

### Estructura de paquetes

```
app/src/main/java/com/example/notaviva/
├── data/
│   ├── local/          SQLiteOpenHelper, contrato de tablas y DAOs
│   └── repository/     CasoRepository
├── domain/model/       Modelos de dominio y enums (EstadoCaso)
├── ui/
│   ├── home/           PantallaInicio
│   ├── cases/          PantallaCasos y TarjetaCaso
│   ├── detail/         PantallaDetalleCaso, pestañas y diálogos
│   ├── form/           PantallaFormularioCaso
│   ├── components/     Composables reutilizables y colores de estado
│   ├── navigation/     Rutas y grafo de navegación
│   └── theme/          Colores, tipografía y tema
├── viewmodel/          CasosViewModel, DetalleCasoViewModel,
│                       FormularioCasoViewModel y sus fábricas
└── MainActivity.kt
```

## Reglas de negocio implementadas

- [x] Crear y guardar caso (título, descripción, tema, fecha y estado)
- [x] Editar caso
- [x] Eliminar caso, con confirmación y borrado en cascada
- [x] Listar y buscar casos por título, descripción o tema
- [x] Filtrar y visualizar el estado de cada caso
- [x] Registrar entrevistas con sus principales hallazgos
- [x] Registrar evidencias, con tipo deducido de la extensión
- [x] Registrar la conclusión del caso
- [x] Gestionar el cierre del caso (un caso cerrado no admite más información)
- [x] Pruebas unitarias

## Pruebas

Las pruebas corren en la JVM, sin emulador:

```bash
./gradlew test
```

| Archivo | Qué verifica |
|---|---|
| `ModelosTest` | Validaciones, búsqueda, estados y tamaños de archivo |
| `ReglasDeNegocioTest` | Guardado, edición, borrado en cascada y cierre de casos |
| `FormularioCasoViewModelTest` | Validación del formulario antes de guardar |

Esto es posible porque los ViewModels dependen de la interfaz
`RepositorioCasos` y no de la implementación: en las pruebas se inyecta
`RepositorioFalso`, que guarda todo en memoria.

## Cómo ejecutar

1. Clonar el repositorio y abrir la carpeta en Android Studio.
2. Esperar a que Gradle sincronice (descarga Compose y Navigation la primera vez).
3. Ejecutar en un emulador o dispositivo con Android 8.0 (API 26) o superior.

```bash
git clone <url-del-repositorio>
cd parcial1-componentes-301
./gradlew assembleDebug
```

## Convención de commits

Se usa [Conventional Commits](https://www.conventionalcommits.org/):
`feat`, `fix`, `test`, `docs`, `refactor`, `chore`, con el alcance entre
paréntesis. Ejemplo: `feat(cases): agregar búsqueda por título`.
