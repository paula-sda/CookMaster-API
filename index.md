# CookMaster API

**Versión:** 1.0  
**Fecha:** 19/12/2025  
**Base URL:** `https://api.cookmaster.com/v1`

---

## 1. Descripción General
CookMaster API es un servicio REST que permite gestionar recetas de cocina, ingredientes, categorías y valoraciones de usuarios. Está diseñada para aplicaciones móviles, webs gastronómicas y plataformas de planificación de comidas.

**Propósito del API:** Ofrecer un repositorio centralizado de recetas con información detallada, ingredientes estructurados y pasos de preparación.

**Usuarios objetivo:**
- Desarrolladores de apps de cocina
- Plataformas de delivery que muestran recetas
- Apps de nutrición
- Blogs gastronómicos


**Casos de uso principales:**
- Buscar recetas por categoría, dificultad o ingredientes
- Obtener detalles completos de una receta
- Crear nuevas recetas
- Registrar valoraciones de usuarios
- Consultar ingredientes disponibles


---

## 2. Versiones

| Versión | Fecha       | Cambios                                       |
|--------:|-------------|-----------------------------------------------|
| 1.0     | 10/01/2025  | Versión inicial con CRUD de recetas           |
| 1.1     | 02/03/2025  | Añadida paginación y filtros avanzados        |
| 1.2     | 15/05/2025  | Añadido endpoint de búsqueda por ingredientes |
| 1.3     | 01/11/2025  | Añadido sistema de valoraciones y dificultad  |

---

## 3. Autenticación
CookMaster API soporta el método de autenticación: **API Key**. A continuación, se detalla cómo utilizarlos.

### 3.1. API Key
La API Key se envía en la cabecera `X-API-Key` para autenticar la solicitud.

**Ejemplo cURL:**

```bash
curl -X GET "https://api.cookmaster.com/v1/recetas" \
-H "X-API-Key: 12345ABC"
```


## 4. EndPoints


### ENDPOINT 1 — GET /recetas/todas

**Ruta completa:** `https://api.cookmaster.com/v1/recetas/todas`  
**Método:** GET  
**Descripción:** Devuelve todas las recetas disponibles en el sistema sin necesidad de filtros ni parámetros.

#### Parámetros
- Sin parámetros

#### Implementacion
```
from cookmaster import CookMaster

api = CookMaster()
todas = api.get_recetas_todas()
print(todas)
```
#### Respuesta

```json
[
  {
    "id": 1,
    "nombre": "Tarta de queso clásica",
    "categoria": "postres",
    "dificultad": "media",
    "tiempoPreparacion": 45,
    "ingredientes": [
      { "nombre": "Queso crema", "cantidad": "300g" },
      { "nombre": "Huevos", "cantidad": "3 unidades" },
      { "nombre": "Azúcar", "cantidad": "120g" },
      { "nombre": "Galletas", "cantidad": "150g" }
    ],
    "pasos": [
      "Triturar las galletas y mezclarlas con mantequilla.",
      "Batir el queso crema con los huevos y el azúcar.",
      "Verter la mezcla sobre la base de galletas.",
      "Hornear 40 minutos a 180°C."
    ],
    "valoracionMedia": 4.7
  },
  {
    "id": 2,
    "nombre": "Brownie de chocolate",
    "categoria": "postres",
    "dificultad": "facil",
    "tiempoPreparacion": 30,
    "ingredientes": [
      { "nombre": "Chocolate", "cantidad": "200g" },
      { "nombre": "Harina", "cantidad": "100g" },
      { "nombre": "Azúcar", "cantidad": "150g" },
      { "nombre": "Huevos", "cantidad": "2 unidades" }
    ],
    "pasos": [
      "Derretir el chocolate.",
      "Mezclar todos los ingredientes.",
      "Hornear 25 minutos a 180°C."
    ],
    "valoracionMedia": 4.6
  },

  ...

  {
    "id": 50,
    "nombre": "Crema de calabaza especiada",
    "categoria": "sopas",
    "dificultad": "facil",
    "tiempoPreparacion": 35,
    "ingredientes": [
      { "nombre": "Calabaza", "cantidad": "400g" },
      { "nombre": "Cebolla", "cantidad": "1 unidad" },
      { "nombre": "Nata líquida", "cantidad": "50ml" },
      { "nombre": "Pimienta y sal", "cantidad": "al gusto" }
    ],
    "pasos": [
      "Saltear la cebolla.",
      "Añadir la calabaza y cubrir con agua.",
      "Cocinar 20 minutos.",
      "Triturar y añadir la nata."
    ],
    "valoracionMedia": 4.4
  }
]
```
#### Errores

| Código | Descripción       |
|--------|-----------------|
| 500    | Error interno    |

#### Limitaciones

-   Máximo 50 recetas devueltas
-   No admite filtros


### ENDPOINT 2 — GET /recetas/{id}

**Ruta completa:** `https://api.cookmaster.com/v1/recetas/{id}`  
**Método:** GET  
**Descripción:** Devuelve la información completa de una receta.

#### Parámetros
- **Path parameters:**
  - `id` (int, obligatorio)

#### Implementacion
```
from cookmaster import CookMaster

api = CookMaster()
receta = api.get_receta(1)
print(receta)
```
#### Respuesta

```json
{
  "id": 1,
  "nombre": "Tarta de queso clásica",
  "categoria": "postres",
  "dificultad": "media",
  "tiempoPreparacion": 45,
  "ingredientes": [
    { "nombre": "Queso crema", "cantidad": "300g" },
    { "nombre": "Huevos", "cantidad": "3 unidades" },
    { "nombre": "Azúcar", "cantidad": "120g" },
    { "nombre": "Galletas", "cantidad": "150g" }
  ],
  "pasos": [
    "Triturar las galletas y mezclarlas con mantequilla.",
    "Batir el queso crema con los huevos y el azúcar.",
    "Verter la mezcla sobre la base de galletas.",
    "Hornear 40 minutos a 180°C."
  ],
  "valoracionMedia": 4.7
}

```

#### Errores

| Código | Descripción           |
|--------|---------------------|
| 400    | ID inválido          |
| 404    | Receta no encontrada |

```
print(api.get_receta(78))  // 404
print(api.get_receta("abc"))  // 400
```

#### Limitaciones

- El ID debe ser numérico
- Recetas archivadas no se muestran


### ENDPOINT 3 —  DELETE /recetas/{id}

**Ruta completa:** `https://api.cookmaster.com/v1/recetas/{id}`  
**Método:** DELETE  
**Descripción:** Elimina una receta.

#### Parámetros

- **Path parameters:**
  - `id` (int, obligatorio)

#### Implementacion
```
from cookmaster import CookMaster

api = CookMaster()
receta_eliminada = api.eliminar_receta(6)
print(receta_eliminada)
```
#### Respuesta

```
{
  "mensaje": "Receta eliminada correctamente"
}
```

#### Errores

| Código | Descripción          |
|--------|---------------------|
| 400    | ID inválido          |
| 404    | Receta no encontrada |

```
print(api.eliminar_receta(200))  // 404
print(api.eliminar_receta("abc"))  // 400
```

#### Limitaciones

- El ID debe ser numérico

### ENDPOINT 4 — POST /recetas

**Ruta completa:** `https://api.cookmaster.com/v1/recetas`  
**Método:** POST  
**Descripción:** Crea una nueva receta.


#### Parámetros

- **Body / Response fields:**
  - `nombre` (string, obligatorio)  
  - `categoria` (string, obligatorio)  
  - `dificultad` (string, opcional)  
  - `tiempoPreparacion` (int, opcional)  
  - `ingredientes` (array, obligatorio)  
  - `pasos` (array, obligatorio)  
  - `valoracionMedia` (float, opcional)


#### Implementacion
```
nueva = {
  "nombre": "Pasta al pesto",
  "categoria": "pastas",
  "dificultad": "facil",
  "tiempoPreparacion": 20,
  "ingredientes": [
    { "nombre": "Pasta", "cantidad": "200g" },
    { "nombre": "Albahaca fresca", "cantidad": "1 taza" },
    { "nombre": "Piñones", "cantidad": "30g" },
    { "nombre": "Queso parmesano", "cantidad": "40g" },
    { "nombre": "Aceite de oliva", "cantidad": "3 cucharadas" }
  ],
  "pasos": [
    "Hervir la pasta hasta que esté al dente.",
    "Triturar albahaca, piñones, parmesano y aceite para preparar el pesto.",
    "Mezclar la pasta escurrida con el pesto.",
    "Servir caliente con un poco de parmesano extra."
  ],
  "valoracionMedia": 4.5
}


print(api.crear_receta(nueva))
```

#### Respuesta
```
{
  "id": 51,
  "mensaje": "Receta creada correctamente"
}
```
#### Errores

| Código | Descripción                |
|--------|----------------------------|
| 400    | Falta un campo obligatorio |
| 409    | Receta duplicada           |

```
print(api.crear_receta({"categoria": "pastas"}))  # 400
print(api.crear_receta(duplicada))  # 409
```

#### Limitaciones

-   Máximo 20 ingredientes por receta

-   Nombre único obligatorio




## 5. Schemas

### Receta

| Campo             | Tipo   | Obligatorio |
|------------------|--------|-------------|
| id               | int    | No          |
| nombre           | string | Sí          |
| categoria        | string | Sí          |
| dificultad       | string | No          |
| tiempoPreparacion| int    | No          |
| ingredientes     | array  | Sí          |
| pasos            | array  | Sí          |
| valoracionMedia  | float  | No          |

### Estructura json

```json
{
  "id": 1,
  "nombre": "Tarta de queso clásica",
  "categoria": "postres",
  "dificultad": "media",
  "tiempoPreparacion": 45,
  "ingredientes": [
    { "nombre": "Queso crema", "cantidad": "300g" },
    { "nombre": "Huevos", "cantidad": "3 unidades" },
    { "nombre": "Azúcar", "cantidad": "120g" },
    { "nombre": "Galletas", "cantidad": "150g" }
  ],
  "pasos": [
    "Triturar las galletas y mezclarlas con mantequilla.",
    "Batir el queso crema con los huevos y el azúcar.",
    "Verter la mezcla sobre la base de galletas.",
    "Hornear 40 minutos a 180°C."
  ],
  "valoracionMedia": 4.7
}
```


## 6. Errores comunes

#### Errores Comunes y Códigos HTTP

| Código | Significado | Descripción |
|--------|------------|-------------|
| 200    | OK         | Solicitud exitosa. El recurso solicitado se devuelve correctamente. |
| 201    | Created    | Recurso creado correctamente (por ejemplo, POST /recetas). |
| 400    | Bad Request| Solicitud mal formada o con parámetros inválidos. Ej.: ID no numérico, falta de campos obligatorios. |
| 401    | Unauthorized | No se proporcionó credencial válida (API Key, Bearer Token, OAuth). |
| 404    | Not Found  | Recurso no encontrado. Ej.: receta inexistente. |
| 409    | Conflict   | Conflicto con el estado actual del recurso. Ej.: receta duplicada al crear. |
| 429    | Too Many Requests | Se ha excedido el límite de solicitudes por minuto/día. |
| 500    | Internal Server Error | Error inesperado en el servidor. |




## 7. Limitaciones y Cuotas

- 100 solicitudes por minuto  
- 10.000 solicitudes por día  
- Tamaño máximo del body: 1 MB  
- Máximo 50 recetas por página  

---

## 8. SDKs Disponibles

- **SDK Python:** [cookmaster.py](./sdk/cookmaster.py)
- **SDK Java:** [CookMasterSDK.java](./sdk/CookMasterSDK.java)
 


