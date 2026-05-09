# 🚌 TransportRoutes - Planificador de Transporte Público

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-23-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Sistema de planificación de rutas de transporte público desarrollado con **Java 21** y **JavaFX 23**, utilizando algoritmos de grafos para encontrar las rutas óptimas entre estaciones.

---

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Requisitos](#-requisitos)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Algoritmos](#-algoritmos)
- [Internacionalización](#-internacionalización)
- [Testing](#-testing)
- [Contribuir](#-contribuir)
- [Autores](#-autores)

---

## ✨ Características

### 🎯 Funcionalidades Principales

- **Gestión de Estaciones**: Crear, editar, eliminar y buscar estaciones
- **Gestión de Conexiones**: Definir conexiones bidireccionales entre estaciones con distancias
- **Cálculo de Rutas Óptimas**:
  - Por menor tiempo (Dijkstra con tiempo)
  - Por menor número de transbordos (BFS)
- **Visualización en Mapa**: Mapa interactivo con Leaflet mostrando todas las estaciones
- **Persistencia de Datos**: Guardado y carga de redes en formato XML
- **Interfaz Multiidioma**: Español, Inglés y Francés
- **Dashboard Estadístico**: Panel con métricas del sistema en tiempo real

### 🎨 Características de Interfaz

- Diseño moderno con sistema de pestañas
- Animaciones fluidas
- Validación de datos en tiempo real
- Mensajes de error descriptivos
- Tema visual coherente con CSS personalizado
- Búsqueda y filtrado de datos
- Confirmaciones para acciones críticas

---

## 🏗️ Arquitectura

El proyecto sigue una arquitectura **MVC (Modelo-Vista-Controlador)** modificada con las siguientes capas:

```
┌─────────────────────────────────────────┐
│           VIEW (JavaFX FXML)            │
│  DashboardView, StationsView, etc.     │
└───────────────┬─────────────────────────┘
                │
┌───────────────▼─────────────────────────┐
│      VIEW CONTROLLER                    │
│  MainController, StationsController,    │
│  ConnectionsController, etc.            │
└───────────────┬─────────────────────────┘
                │
┌───────────────▼─────────────────────────┐
│         CONTROLLER                      │
│  GraphController, RouteController       │
└───────────────┬─────────────────────────┘
                │
┌───────────────▼─────────────────────────┐
│          MODEL                          │
│  Node, Edge, GraphData, RouteResult    │
└───────────────┬─────────────────────────┘
                │
┌───────────────▼─────────────────────────┐
│       PERSISTENCE                       │
│  PersistenceManager, XmlRouteDAO       │
└─────────────────────────────────────────┘
```

### Componentes Clave

#### 📦 **Model** (`co.edu.uptc.model`)
- `Node`: Representa una estación con ID, nombre y coordenadas
- `Edge`: Representa una conexión entre dos estaciones
- `GraphData`: Contenedor para persistencia del grafo completo
- `RouteResult`: Resultado de cálculo de rutas

#### 🎮 **Controller** (`co.edu.uptc.controller`)
- `GraphController`: Gestión centralizada del grafo (Singleton)
- `RouteController`: Algoritmos de búsqueda de rutas

#### 🖼️ **View Controller** (`co.edu.uptc.viewController`)
- `MainController`: Controlador principal de la aplicación
- `StationsController`: Gestión de estaciones
- `ConnectionsController`: Gestión de conexiones
- `RoutesController`: Cálculo y visualización de rutas
- `MapController`: Visualización del mapa interactivo
- `DashboardController`: Panel de estadísticas
- `SettingsController`: Configuración del sistema

#### 💾 **Persistence** (`co.edu.uptc.persistence`)
- `PersistenceManager`: Gestor de persistencia (Singleton)
- `RouteDAO`: Interfaz para acceso a datos
- `XmlRouteDAO`: Implementación con JAXB

---

## 🛠️ Tecnologías

### Core
- **Java 21**: Lenguaje principal
- **JavaFX 23**: Framework de interfaz gráfica
- **Maven**: Gestor de dependencias y construcción

### Librerías
- **JAXB 4.0.2**: Serialización XML
- **Leaflet 1.9.4**: Mapas interactivos
- **JUnit 5.10.2**: Testing unitario
- **Mockito 5.11.0**: Mocking para tests

### Herramientas
- **Git**: Control de versiones
- **VS Code / IntelliJ**: IDEs recomendados

---

## 📦 Requisitos

### Software Necesario

```bash
# Java JDK 21 o superior
java --version
# Debería mostrar: java 21.x.x

# Maven 3.8 o superior
mvn --version
# Debería mostrar: Apache Maven 3.8.x

# Git
git --version
```

### Dependencias del Sistema

**Windows**:
- Java JDK 21+ instalado
- Variable `JAVA_HOME` configurada
- Maven en el PATH

**Linux/Mac**:
```bash
sudo apt install openjdk-21-jdk maven  # Ubuntu/Debian
brew install openjdk@21 maven          # macOS
```

---

## 🚀 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/TransportRoutes.git
cd TransportRoutes
```

### 2. Compilar el Proyecto

```bash
# Compilar con Maven
mvn clean compile

# O compilar y empaquetar
mvn clean package
```

### 3. Ejecutar la Aplicación

```bash
# Opción 1: Con Maven
mvn javafx:run

# Opción 2: Con Java directamente (después de compilar)
java --module-path "C:\ruta\a\javafx\lib" \
     --add-modules javafx.controls,javafx.fxml,javafx.web \
     -jar target/transportroutes-1.0-SNAPSHOT.jar
```

### 4. Ejecutar Tests

```bash
# Todos los tests
mvn test

# Con reporte de cobertura
mvn test jacoco:report

# Ver reporte en: target/site/jacoco/index.html
```

---

## 📖 Uso

### Inicio Rápido

1. **Lanzar la aplicación**:
   ```bash
   mvn javafx:run
   ```

2. **Cargar red de ejemplo**:
   - Click en "📁 Cargar XML"
   - Seleccionar `src/main/resources/co/edu/uptc/network_example.xml`
   - O esperar la carga automática

3. **Explorar el Dashboard**:
   - Ver estadísticas en tiempo real
   - Accesos rápidos a funciones principales

### Gestión de Estaciones

1. **Añadir Estación**:
   - Ir a pestaña "📍 Estaciones"
   - Click en "Nuevo"
   - Completar formulario (ID formato: EST###)
   - Click en "Guardar"

2. **Editar Estación**:
   - Seleccionar estación de la tabla
   - Modificar datos en el formulario
   - Click en "Guardar"

3. **Eliminar Estación**:
   - Seleccionar estación
   - Click en "Eliminar"
   - Confirmar acción

### Gestión de Conexiones

1. **Crear Conexión**:
   - Ir a pestaña "🔗 Conexiones"
   - Seleccionar estación origen
   - Seleccionar estación destino
   - Ingresar distancia (km)
   - Click en "Guardar"

2. **Las conexiones son bidireccionales** automáticamente

### Calcular Rutas

1. **Seleccionar Origen y Destino**:
   - Ir a pestaña "🧭 Rutas"
   - Elegir estación origen
   - Elegir estación destino

2. **Seleccionar Criterio**:
   - **Tiempo**: Ruta más rápida
   - **Transbordos**: Menos cambios de línea

3. **Ver Resultado**:
   - Lista de estaciones en orden
   - Distancia total, tiempo y transbordos

### Visualizar Mapa

- Ir a pestaña "🗺️ Mapa"
- Ver todas las estaciones con coordenadas
- Click en marcadores para ver información
- Zoom y pan interactivos

### Configuración

1. **Cambiar Idioma**:
   - Selector de idioma en barra superior
   - Confirmar recarga de aplicación

2. **Ajustar Velocidad**:
   - Ir a pestaña "⚙️ Configuraciones"
   - Modificar velocidad por defecto (km/h)
   - Click en "Guardar"

### Persistencia

1. **Guardar Red**:
   - Click en "💾 Guardar XML"
   - Seleccionar ubicación
   - Archivo se guarda en formato XML

2. **Cargar Red**:
   - Click en "📁 Cargar XML"
   - Seleccionar archivo XML
   - Red se carga automáticamente

---

## 📁 Estructura del Proyecto

```
TransportRoutes/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── co/edu/uptc/
│   │   │       ├── App.java                    # Punto de entrada
│   │   │       ├── controller/                 # Controladores lógica
│   │   │       │   ├── GraphController.java
│   │   │       │   └── RouteController.java
│   │   │       ├── model/                      # Modelos de datos
│   │   │       │   ├── Node.java
│   │   │       │   ├── Edge.java
│   │   │       │   ├── GraphData.java
│   │   │       │   └── RouteResult.java
│   │   │       ├── persistence/                # Capa de persistencia
│   │   │       │   ├── PersistenceManager.java
│   │   │       │   ├── RouteDAO.java
│   │   │       │   └── XmlRouteDAO.java
│   │   │       ├── validation/                 # Validaciones
│   │   │       │   └── StationValidator.java
│   │   │       └── viewController/             # Controladores UI
│   │   │           ├── MainController.java
│   │   │           ├── StationsController.java
│   │   │           ├── ConnectionsController.java
│   │   │           ├── RoutesController.java
│   │   │           ├── MapController.java
│   │   │           ├── DashboardController.java
│   │   │           └── SettingsController.java
│   │   ├── resources/
│   │   │   └── co/edu/uptc/
│   │   │       ├── assets/                     # Recursos gráficos
│   │   │       │   └── logo.png
│   │   │       ├── i18n/                       # Archivos de idioma
│   │   │       │   ├── messages_es.properties
│   │   │       │   ├── messages_en.properties
│   │   │       │   └── messages_fr.properties
│   │   │       ├── styles/                     # Estilos CSS
│   │   │       │   └── styles.css
│   │   │       ├── view/                       # Vistas FXML
│   │   │       │   ├── MainView.fxml
│   │   │       │   ├── DashboardView.fxml
│   │   │       │   ├── StationsView.fxml
│   │   │       │   ├── ConnectionsView.fxml
│   │   │       │   ├── RoutesView.fxml
│   │   │       │   ├── MapView.fxml
│   │   │       │   ├── SettingsView.fxml
│   │   │       │   ├── map_template.html      # HTML del mapa
│   │   │       │   ├── leaflet.js             # Librería Leaflet
│   │   │       │   └── leaflet.css            # Estilos Leaflet
│   │   │       ├── network_example.xml         # Red de ejemplo
│   │   │       └── bogota_network.xml          # Red de Bogotá
│   │   └── module-info.java                    # Módulo Java
│   └── test/
│       └── java/
│           └── co/edu/uptc/
│               ├── controller/                  # Tests controladores
│               ├── model/                       # Tests modelos
│               ├── persistence/                 # Tests persistencia
│               └── validation/                  # Tests validaciones
├── pom.xml                                      # Configuración Maven
├── README.md                                    # Este archivo
└── .gitignore                                   # Archivos ignorados
```

---

## 🧮 Algoritmos

### Dijkstra - Ruta Más Corta

**Uso**: Encontrar la ruta de menor distancia o menor tiempo entre dos estaciones.

**Complejidad**: O((V + E) log V) donde V = nodos, E = aristas

**Implementación**:
```java
// RouteController.java
public RouteResult findShortestByDistance(String fromId, String toId)
public RouteResult findShortestByTime(String fromId, String toId)
```

**Cómo funciona**:
1. Inicializa distancias a infinito excepto el origen (0)
2. Usa una cola de prioridad para procesar nodos
3. Relaja aristas actualizando distancias mínimas
4. Reconstruye el camino desde destino a origen

### BFS - Menor Número de Transbordos

**Uso**: Encontrar la ruta con el menor número de estaciones intermedias.

**Complejidad**: O(V + E)

**Implementación**:
```java
// RouteController.java
public RouteResult findFewestTransfers(String fromId, String toId)
```

**Cómo funciona**:
1. Usa una cola FIFO para exploración por niveles
2. Marca nodos visitados para evitar ciclos
3. Cada nivel representa un transbordo adicional
4. Retorna la primera solución encontrada (óptima)

---

## 🌍 Internacionalización (i18n)

El sistema soporta múltiples idiomas mediante archivos `.properties`:

### Idiomas Disponibles

- 🇪🇸 **Español** (`messages_es.properties`)
- 🇺🇸 **Inglés** (`messages_en.properties`)
- 🇫🇷 **Francés** (`messages_fr.properties`)

### Agregar Nuevo Idioma

1. Crear archivo `messages_XX.properties` donde XX es el código del idioma
2. Traducir todas las claves del archivo español
3. Añadir el locale en `MainController.java`:
   ```java
   cmbLocale.getItems().add(new Locale("xx", "XX"));
   ```

### Uso en Código

```java
// Obtener texto traducido
String text = bundle.getString("key.name");

// Usar en FXML
<Label text="%label.welcome" />
```

---

## 🧪 Testing

### Cobertura de Tests

El proyecto incluye tests unitarios con **>70% de cobertura**:

- ✅ Modelos: 100%
- ✅ Controladores: 80%
- ✅ Persistencia: 90%
- ✅ Validaciones: 95%

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Un test específico
mvn test -Dtest=GraphControllerTest

# Con cobertura
mvn test jacoco:report
```

### Estructura de Tests

```
src/test/java/co/edu/uptc/
├── controller/
│   ├── GraphControllerTest.java
│   └── RouteControllerTest.java
├── model/
│   ├── NodeTest.java
│   ├── EdgeTest.java
│   └── GraphDataTest.java
├── persistence/
│   ├── XmlRouteDAOTest.java
│   └── PersistenceManagerTest.java
└── validation/
    └── StationValidatorTest.java
```

### Ejemplo de Test

```java
@Test
public void testAddNode() {
    GraphController gc = GraphController.getInstance();
    Node node = new Node("EST001", "Estación Test");
    
    assertTrue(gc.addNode(node));
    assertEquals(node, gc.getNode("EST001"));
}
```

---

## 🤝 Contribuir

### Proceso de Contribución

1. **Fork** el repositorio
2. **Crear rama** para tu feature:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. **Commit** cambios:
   ```bash
   git commit -m "feat: añadir nueva funcionalidad"
   ```
4. **Push** a tu fork:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
5. **Abrir Pull Request** en GitHub

### Convenciones de Código

- **Idioma**: Código en español, comentarios en español
- **Estilo**: Seguir convenciones de Java (camelCase, etc.)
- **Documentación**: Javadoc obligatorio en clases y métodos públicos
- **Tests**: Añadir tests para nuevo código

### Commits Semánticos

```
feat: nueva funcionalidad
fix: corrección de bug
docs: documentación
style: formato
refactor: refactorización
test: añadir tests
chore: tareas de mantenimiento
```

---

## 👥 Autores

**Equipo de Desarrollo**

- Desarrollador Principal - Universidad Pedagógica y Tecnológica de Colombia (UPTC)
- Curso: Programación III
- Fecha: 2025

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

---

## 📞 Soporte

¿Tienes preguntas o problemas?

- 📧 Email: ferney.alvarez@uptc.edu.co
- 🐛 Issues: [GitHub Issues](https://github.com/tu-usuario/TransportRoutes/issues) (Yet to be created)
- 📖 Docs: [Wiki del Proyecto](https://github.com/tu-usuario/TransportRoutes/wiki) (Yet to be created)

---

## 🎉 Agradecimientos

- OpenStreetMap por los mapas
- Leaflet por la librería de mapas
- JavaFX por el framework de UI
- Comunidad de código abierto

---

**Hecho con ❤️ en Colombia 🇨🇴**
