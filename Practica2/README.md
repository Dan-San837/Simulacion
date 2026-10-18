# Modelos Matemáticos de Crecimiento Poblacional (Practica 2)

## 1. Descripción del proyecto

- **Objetivo:** Simular y comparar diferentes modelos matemáticos para predecir el crecimiento poblacional.
- **Propósito:** Analizar el comportamiento de un sistema poblacional mediante modelos determinístico, discreto, estocástico y continuo, permitiendo comparar sus resultados con datos observados.
- **Problema resuelto:** Se automatiza la estimación de parámetros (tasa de crecimiento y variabilidad), la ejecución de los cuatro modelos, la visualización comparativa y la interpretación de sus diferencias, evitando cálculos manuales.

### Modelos utilizados

#### Modelo determinístico
Se utiliza el crecimiento exponencial:

$$P(t) = P_0 e^{rt}$$

Donde:
* $P_0$: población inicial.
* $P(t)$: población en el tiempo $t$.
* $r$: tasa de crecimiento.
* $t$: tiempo.

No se utiliza aleatoriedad.

#### Modelo discreto
Se utiliza:

$$P_{t+1} = P_t + r P_t$$

La población cambia en intervalos de tiempo definidos.

#### Modelo estocástico
Se incorpora una variable aleatoria:

$$P_{t+1} = P_t + r P_t + \epsilon_t$$

Donde:

$$\epsilon_t \sim N(0, \sigma^2)$$

y $\sigma$ representa el nivel de variabilidad del sistema.

#### Modelo continuo
Se utiliza la ecuación diferencial:

$$\frac{dP}{dt} = r P$$

mediante una aproximación numérica de Euler:

$$P_{t+\Delta t} = P_t + \Delta t (r P_t)$$

donde $\Delta t$ es el tamaño del paso.

## 2. Tecnologías utilizadas

- **Lenguajes:** Python 3.x
- **Librerías:** `numpy` (cálculos numéricos), `matplotlib` (generación e integración de gráficas), `statistics` (cálculo de media y varianza poblacional de residuos).

## 3. Instalación

### 3.1. Clonar el repositorio

```bash
git clone https://github.com/Dan-San837/Simulacion.git
cd Simulacion/Practica2
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
python Main.py
# Linux y MacOS
python3 Main.py
```

### 4.2. Ejemplos de uso

- Ejecutar únicamente el cálculo y visualización local: `python Main.py`
- Modificar los datos observados en `Main.py` para analizar distintos escenarios poblacionales.

## 5. Estructura del proyecto

| Carpeta/Archivo | Descripción |
|-----------------|-------------|
| `Models/Deterministico.py` | Implementa el modelo exponencial continuo en forma analítica: $P(t)=P_0 e^{rt}$. |
| `Models/Discreto.py` | Implementa el modelo en diferencias: $P_{t+1}=P_t + r P_t$. |
| `Models/Estocastico.py` | Implementa el modelo con ruido gaussiano: $P_{t+1}=P_t + r P_t + \epsilon_t$, $\epsilon_t \sim N(0,\sigma^2)$. |
| `Models/Continuo.py` | Implementa la solución numérica de Euler para $\frac{dP}{dt}=rP$. |
| `Controlador.py` | Estima parámetros ($P_0$, $r$, $\sigma$) a partir de datos observados, crea los modelos, ejecuta las simulaciones y genera las gráficas. |
| `Main.py` | Punto de entrada: define los datos de ejemplo, instancia el controlador y muestra la comparación gráfica. |
| `requirements.txt` | Dependencias de Python. |
| `README.md` | Documentación del proyecto. |

## 6. Información adicional

- **Funcionalidades:** Estimación de tasa de crecimiento a partir de datos observados, cálculo de variabilidad del sistema, simulación de cuatro enfoques (determinístico, discreto, estocástico y continuo), generación de gráficas comparativas y normalización/validación básica de datos.
- **Capturas:**
    - Comparación de modelos de crecimiento poblacional
    <img width="580" height="500" alt="image" src="https://github.com/Dan-San837/Simulacion/blob/develop/docs/Practica2.png?raw=true">

### 6.1. Declaraciòn de uso de IA
**Herramienta utilizada:** OpenCode

**Forma de Uso:** La herramienta fue utilizada como apoyo para revisar, corregir e identificar errores en la estructura del còdigo desarrollado.

**Alcance de su intervenciòn:** Medio

---

## Flujo del proyecto

1. **Definición:** Establece el objetivo, propósito y problema a resolver mediante modelos de crecimiento poblacional.
2. **Tecnologías:** Identifica herramientas, lenguajes y librerías necesarias.
3. **Instalación:** Clonación, configuración de entorno virtual e instalación de dependencias con `pip install`.
4. **Uso:** Ejecución con `python Main.py` o `python3 Main.py` para obtener resultados y gráficas comparativas.
5. **Estructura:** Comprende el papel de cada módulo, permite la extensión con nuevos modelos o fuentes de datos y facilita el análisis de resultados.
