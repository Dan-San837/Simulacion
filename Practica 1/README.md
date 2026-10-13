# Modelo Matemático de Predicción de Lluvia (Practica 1)

## 1. Descripción del proyecto

- **Objetivo:** Simular el comportamiento de la atmósfera durante 24 horas para predecir la posibilidad de lluvia mediante un modelo matemático, permitiendo la selección y visualización de los resultados.
- **Propósito:** Analizar la relación entre humedad, nubosidad y temperatura para obtener un índice de probabilidad de lluvia.
- **Problema resuelto:** Se automatiza el cálculo, la clasificación y la visualización del índice de lluvia para un conjunto de observaciones horarias, evitando cálculos manuales y facilitando su interpretación.

**Fórmula del modelo:**

`I = 0.5*H + 0.3*N + 0.2*Tf`

- `H`: Humedad normalizada (0.0 a 1.0) = Humedad (%) / 100
- `N`: Nubosidad normalizada (0.0 a 1.0) = Nubosidad (%) / 100
- `Tf`: Factor de temperatura asignado según rangos de temperatura en °C

**Reglas del Índice (I) y Estado:**

| Rango de I       | Estado           |
|------------------|------------------|
| I < 0.40         | Sin lluvia       |
| 0.40 ≤ I < 0.60  | Baja posibilidad |
| 0.60 ≤ I < 0.75  | Lluvia probable  |
| I ≥ 0.75         | Lluvia           |

## 2. Tecnologías utilizadas

- **Lenguajes:** Python 3.x
- **Librerías:** `matplotlib` (generación e integración de gráficas).


## 3. Instalación

### 3.1. Clonar el repositorio

```bash
git clone https://github.com/Dan-San837/Simulacion.git
cd Simulacion/Practica 1
```

### 3.2. Backend (Python)

```bash
# Crear y activar entorno virtual
python -m venv venv
# Windows
venv\Scripts\activate
# Linux/MacOS
source venv/bin/activate

# Instalar dependencias
pip install -r requirements.txt
```


## 4. Uso

### 4.1. Ejecutar Backend (Python)

```bash
# Windows
python "Vista (Main).py"
# Linux y MacOS
python3 "Vista (Main).py"
```

### 4.2. Ejemplos de uso

- Ejecutar únicamente el cálculo y visualización local: `python "Vista (Main).py"`


## 5. Estructura del proyecto

| Carpeta/Archivo | Descripción |
|-----------------|-------------|
| `Controlador.py` | Recibe los datos puros del problema, los normaliza (H, N) y los prepara para el modelo. |
| `Modelo.py` | Contiene la lógica matemática, asignación de `Tf`, cálculo de `I` y reglas de decisión. |
| `Vista (Main).py` | Almacena los datos, los despliega en consola, calcula resultados y genera las gráficas con `matplotlib`. |
| `requirements.txt` | Dependencias de Python. |
| `README.md` | Documentación del proyecto. |
| `Practica1.pdf` |Reporte Tècnico de Actividades Práctico-Expermentales Nro. 001|

## 6. Información adicional

- **Funcionalidades:** Cálculo del índice de lluvia, clasificación por estado, generación de gráficas, normalización de datos y soporte para ajuste del modelo.
- **Capturas:** 
    - Grafica Indice 
    <img width="580" height="500" alt="image" src="https://github.com/Dan-San837/Simulacion/blob/develop/docs/Indice.png?raw=true">


    - Grafica Indice Ajustado
    <img width="580" height="500" alt="image" src="https://github.com/Dan-San837/Simulacion/blob/develop/docs/Indice%20ajustado.jpeg?raw=true">

### 6.1. Declaraciòn de uso de IA
**Herramienta utilizada:** Gemini

**Forma de Uso:** La herramienta fue utilizada como apoyo para revisar, corregir e identificar errores en la estructura del còdigo desarrollado.

**Alcance de su intervenciòn:** Medio-bajo

---

## Flujo del proyecto

1. **Qué hace:** Definición del objetivo, propósito y problema a resolver.
2. **Tecnologías:** Identificación de herramientas, lenguajes y medidas de seguridad.
3. **Instalación:** Clonación, configuración de entorno, `pip install`, `npm install` y revisión/merge a `main`.
4. **Uso:** Ejecución con `npm start`, `python app.py` / `python "Vista (Main).py"`, validación de autenticidad.
5. **Estructura:** Conocimiento de módulos, detección de errores y registro de fallos.
