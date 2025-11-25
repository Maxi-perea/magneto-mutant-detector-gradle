Global Integrador - Máximo Perea 51033 3K09
# 🧬 Mutant Detector API

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-green) ![Coverage](https://img.shields.io/badge/Coverage->80%25-brightgreen)

API REST desarrollada para identificar mutantes basándose en su secuencia de ADN. Proyecto realizado como desafío técnico para **MercadoLibre / Magneto**.

🔗 **URL de la API en Producción:** [https://magneto-mutant-detector-gradle.onrender.com](https://magneto-mutant-detector-gradle.onrender.com)

---

## 📋 Descripción del Proyecto

Magneto quiere reclutar la mayor cantidad de mutantes para su ejército. Este sistema permite detectar si un humano es mutante analizando su secuencia de ADN mediante un array de Strings.

**Regla de Negocio:**
Un humano es mutante si se encuentran **más de una secuencia de cuatro letras iguales** (A, T, C, G) en dirección oblicua, horizontal o vertical.

### 🚀 Tecnologías Utilizadas
* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.2.3
* **Base de Datos:** H2 Database (En memoria, optimizada con índices).
* **Testing:** JUnit 5 + Mockito.
* **Despliegue:** Docker + Render.
* **Build Tool:** Gradle.
* **IA:** Gemini 3 Pro.
* **Documentación Swagger:** [Ver Documentación Interactiva](https://magneto-mutant-detector-gradle.onrender.com/swagger-ui/index.html)

---

## ⚡ API Endpoints

### 1. Detectar Mutante
Analiza una secuencia de ADN y guarda el resultado en la base de datos.

* **Método:** `POST`
* **URL:** `/mutant/`
* **Headers:** `Content-Type: application/json`

**Ejemplo de Body (Mutante):**
```json
{
    "dna": [
        "ATGCGA",
        "CAGTGC",
        "TTATGT",
        "AGAAGG",
        "CCCCTA",
        "TCACTG"
    ]
}
```

## 🚀 Instrucciones de Ejecución

### Prerrequisitos
* Java 21 (JDK)
* Git (Opcional, para clonar)

### 1. Ejecutar la API localmente
Si deseas levantar el servidor en tu propia máquina (localhost:8080):

```bash
# Clonar el repositorio
git clone [https://github.com/TU_USUARIO/mutant-detector.git](https://github.com/TU_USUARIO/mutant-detector.git)

# Entrar a la carpeta
cd mutant-detector

# Ejecutar con Maven Wrapper (Windows)
./mvnw spring-boot:run

# Ejecutar con Maven Wrapper (Linux/Mac)
./mvnw spring-boot:run
```
## 📖 Documentación API (Swagger UI)

El proyecto implementa **OpenAPI (Swagger)** para documentar y probar la API visualmente. No es necesario instalar herramientas externas como Postman.

👉 **[Acceder a la Documentación Interactiva](https://magneto-mutant-detector-gradle.onrender.com/swagger-ui/index.html)**

Desde esta interfaz podrás:
1. Ver los esquemas de datos JSON (`DnaRequest`, `StatsResponse`).
2. Probar los endpoints `POST /mutant/` y `GET /stats` con el botón "Try it out".
3. Ver los códigos de respuesta HTTP posibles (200, 403, 400).


## 📊 Diagrama de Secuencia

Flujo de ejecución cuando se recibe un ADN:

```mermaid
sequenceDiagram
    participant Client as Cliente (Postman/cURL)
    participant Controller as MutantController
    participant Service as MutantService
    participant Repo as DnaRepository
    participant DB as H2 Database

    Client->>Controller: POST /mutant/ {dna}
    Note over Controller: Valida JSON (@Valid)
    Controller->>Service: analyze(dna)
    
    Service->>Service: Calcular Hash del ADN
    
    Service->>Repo: findByDnaHash(hash)
    Repo->>DB: SELECT ... WHERE hash = ?
    
    alt Ya existe en BD (Cache)
        DB-->>Repo: Registro encontrado
        Repo-->>Service: DnaRecord
        Service-->>Controller: return isMutant (booleano)
    else Es Nuevo
        DB-->>Repo: null
        Repo-->>Service: empty
        Note over Service: Ejecutar Algoritmo (N*N)
        Service->>Service: isMutantAlgorithm(dna)
        Service->>Repo: save(newRecord)
        Repo->>DB: INSERT ...
        Service-->>Controller: return result
    end

    alt result == true
        Controller-->>Client: 200 OK
    else result == false
        Controller-->>Client: 403 Forbidden
    end
```
