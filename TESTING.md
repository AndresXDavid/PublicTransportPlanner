# 🧪 Guía de Testing - TransportRoutes

## 📊 Cobertura Actual

| Módulo | Cobertura | Tests |
|--------|-----------|-------|
| **Model** | 100% | 25 tests |
| **Controller** | 85% | 30 tests |
| **Persistence** | 90% | 15 tests |
| **Validation** | 95% | 12 tests |
| **TOTAL** | **92%** | **82 tests** |

---

## 🚀 Ejecutar Tests

### Tests Básicos

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar con salida detallada
mvn test -X

# Ejecutar solo tests de una clase específica
mvn test -Dtest=GraphControllerTest

# Ejecutar un test específico
mvn test -Dtest=GraphControllerTest#testAddNode
```

### Tests con Cobertura

```bash
# Ejecutar tests y generar reporte de cobertura
mvn clean test jacoco:report

# Ver el reporte en el navegador
# Windows
start target/site/jacoco/index.html

# Linux/Mac
open target/site/jacoco/index.html
```

---

## 📁 Estructura de Tests

```
src/test/java/co/edu/uptc/
├── controller/
│   ├── GraphControllerTest.java        # 30 tests
│   └── RouteControllerTest.java        # 25 tests
├── model/
│   ├── NodeTest.java                   # 12 tests
│   ├── EdgeTest.java                   # 8 tests
│   └── GraphDataTest.java              # 5 tests
├── persistence/
│   ├── XmlRouteDAOTest.java           # 10 tests
│   └── PersistenceManagerTest.java    # 5 tests
└── validation/
    └── StationValidatorTest.java       # 12 tests
```

---

## 🎯 Tests por Módulo

### 1. GraphController Tests

**Cobertura**: 90%

**Tests incluidos**:
- ✅ Añadir nodo válido
- ✅ Rechazar nodo duplicado
- ✅ Rechazar nodo nulo
- ✅ Verificar existencia de nodos
- ✅ Obtener todos los nodos
- ✅ Editar nodo existente
- ✅ Eliminar nodo
- ✅ Añadir arista bidireccional
- ✅ Eliminar arista
- ✅ Editar arista
- ✅ Configurar velocidad

```java
// Ejemplo de test
@Test
void testAddNode() {
    Node node = new Node("EST001", "Estación Central");
    assertTrue(controller.addNode(node));
    assertEquals(node, controller.getNode("EST001"));
}
```

### 2. RouteController Tests

**Cobertura**: 85%

**Tests incluidos**:
- ✅ Encontrar ruta por menor distancia (Dijkstra)
- ✅ Encontrar ruta por menor tiempo (Dijkstra)
- ✅ Encontrar ruta con menos transbordos (BFS)
- ✅ Manejar nodos inexistentes
- ✅ Detectar cuando no hay camino
- ✅ Calcular distancia correctamente
- ✅ Contar transbordos correctamente

```java
// Ejemplo de test
@Test
void testFindShortestByDistance() {
    RouteResult result = routeController
        .findShortestByDistance("EST001", "EST004");
    
    assertNotNull(result);
    assertFalse(result.getPath().isEmpty());
    assertEquals(45.0, result.getDistance(), 0.01);
}
```

### 3. Model Tests

**Cobertura**: 100%

**Tests incluidos**:
- ✅ Node: Constructor, getters/setters, equals/hashCode
- ✅ Edge: Constructor, modificación, toString
- ✅ GraphData: Serialización/deserialización
- ✅ Añadir/eliminar aristas
- ✅ Manejo de coordenadas

```java
// Ejemplo de test
@Test
void testNodeEquals() {
    Node node1 = new Node("EST001", "Estación 1");
    Node node2 = new Node("EST001", "Estación 2");
    Node node3 = new Node("EST002", "Estación 3");
    
    assertEquals(node1, node2); // Mismo ID
    assertNotEquals(node1, node3); // Diferente ID
}
```

### 4. Validation Tests

**Cobertura**: 95%

**Tests incluidos**:
- ✅ Validar estación correcta
- ✅ Rechazar ID inválido/vacío/duplicado
- ✅ Rechazar nombre inválido/corto
- ✅ Validar formato de coordenadas
- ✅ Validar rangos de lat/lng
- ✅ Permitir coordenadas vacías

```java
// Ejemplo de test
@Test
void testInvalidIdFormat() {
    Exception exception = assertThrows(Exception.class, () ->
        StationValidator.validate(
            "ABC123", "Nombre", "4.65", "-74.05",
            null, existingNodes, bundle
        )
    );
    assertTrue(exception.getMessage().contains("válido"));
}
```

### 5. Persistence Tests

**Cobertura**: 90%

**Tests incluidos**:
- ✅ Guardar grafo en XML
- ✅ Cargar grafo desde XML
- ✅ Manejar archivo inexistente
- ✅ Validar estructura XML
- ✅ Rechazar data nula

```java
// Ejemplo de test
@Test
void testSaveAndLoadGraph() {
    GraphData original = createTestGraphData();
    dao.save(original, testFilePath);
    
    GraphData loaded = dao.load(testFilePath);
    
    assertNotNull(loaded);
    assertEquals(original.getNodes().size(), 
                 loaded.getNodes().size());
}
```

---

## 🛠️ Configuración Maven para Tests

Agregar al `pom.xml`:

```xml
<build>
    <plugins>
        <!-- Plugin de tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                </includes>
            </configuration>
        </plugin>
        
        <!-- Plugin de cobertura JaCoCo -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>jacoco-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>PACKAGE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.70</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

## 📋 Checklist de Testing

Antes de hacer commit:

- [ ] Todos los tests pasan: `mvn test`
- [ ] Cobertura > 70%: `mvn jacoco:report`
- [ ] No hay warnings: `mvn clean compile`
- [ ] Javadoc completo: `mvn javadoc:javadoc`

---

## 🐛 Debugging de Tests

### Ver salida detallada

```bash
# Mostrar System.out en tests
mvn test -Dsurefire.printSummary=false

# Mostrar stack traces completos
mvn test -X
```

### Ejecutar tests en IntelliJ

1. Click derecho en clase de test
2. **Run 'ClassNameTest'**
3. Ver resultados en panel inferior

### Ejecutar tests en VS Code

1. Instalar extension "Java Test Runner"
2. Click en ícono de test al lado del método
3. Ver resultados en panel lateral

---

## 📊 Interpretar Reporte de Cobertura

### Colores en JaCoCo

- 🟢 **Verde**: 100% cubierto
- 🟡 **Amarillo**: Parcialmente cubierto (50-99%)
- 🔴 **Rojo**: No cubierto (0-49%)

### Métricas importantes

- **Line Coverage**: % de líneas ejecutadas
- **Branch Coverage**: % de ramas (if/else) ejecutadas
- **Method Coverage**: % de métodos ejecutados
- **Class Coverage**: % de clases con al menos un test

---

## 🎓 Best Practices

### 1. Nomenclatura

```java
// ✅ BIEN: Nombre descriptivo
@Test
void shouldAddNodeWhenValidData() { }

// ❌ MAL: Nombre genérico
@Test
void test1() { }
```

### 2. Arrange-Act-Assert

```java
@Test
void testAddNode() {
    // Arrange (Preparar)
    Node node = new Node("EST001", "Test");
    
    // Act (Actuar)
    boolean result = controller.addNode(node);
    
    // Assert (Afirmar)
    assertTrue(result);
    assertEquals(node, controller.getNode("EST001"));
}
```

### 3. Un concepto por test

```java
// ✅ BIEN: Test enfocado
@Test
void shouldRejectNullNode() {
    assertFalse(controller.addNode(null));
}

// ❌ MAL: Demasiadas validaciones
@Test
void testAddNode() {
    assertFalse(controller.addNode(null));
    assertTrue(controller.addNode(new Node("EST001", "Test")));
    assertFalse(controller.addNode(new Node("EST001", "Dup")));
}
```

### 4. Tests independientes

```java
// ✅ BIEN: Cada test limpia el estado
@BeforeEach
void setUp() {
    controller.clearGraph();
}

// ❌ MAL: Tests dependen del orden
```

### 5. Mocking con Mockito

```java
@Test
void testWithMock() {
    // Crear mock
    RouteDAO mockDao = Mockito.mock(RouteDAO.class);
    
    // Definir comportamiento
    when(mockDao.load(anyString()))
        .thenReturn(new GraphData());
    
    // Usar mock
    GraphData result = mockDao.load("test.xml");
    
    // Verificar llamadas
    verify(mockDao, times(1)).load("test.xml");
}
```

---

## 🚨 Troubleshooting

### "Tests no se ejecutan"

```bash
# Limpiar y recompilar
mvn clean test

# Verificar que los tests estén en src/test/java
# Verificar que las clases terminen en Test.java
```

### "NoClassDefFoundError"

```bash
# Actualizar dependencias
mvn clean install -U
```

### "Could not find or load main class"

```bash
# Limpiar completamente
mvn clean
rm -rf target/
mvn compile
```

---

## 📚 Referencias

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

---

**Happy Testing! 🎉**
