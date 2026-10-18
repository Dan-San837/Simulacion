# Simulación de un Sistema de Colas M/M/1 (Practica 3)

## 1. Descripción del proyecto

- **Objetivo:** Simular un sistema de líneas de espera M/M/1 con SimPy y comparar los resultados obtenidos con las fórmulas teóricas del modelo.
- **Propósito:** Analizar el comportamiento del servidor y la cola bajo distintos niveles de utilización, validando la teoría mediante la simulación y la visualización de los resultados.
- **Problema resuelto:** Se automatiza la ejecución de los escenarios, el cálculo de las medidas teóricas y simuladas (ρ, L, Lq, W, Wq), la comparación con porcentaje de error y la generación de tablas, animaciones y gráficas, evitando cálculos manuales y facilitando la interpretación.

### Fundamento teórico

El modelo **M/M/1** representa un sistema de líneas de espera que posee:

- Llegadas de clientes siguiendo una distribución de Poisson.
- Tiempos de servicio con distribución exponencial.
- Un único servidor.
- Atención de clientes bajo el principio FIFO: primero en llegar, primero en ser atendido.

El modelo utiliza dos parámetros principales:

- $\lambda$: tasa promedio de llegada de clientes.
- $\mu$: tasa promedio de servicio del servidor.
- $\rho$ (rho): utilización del servidor.

La utilización del servidor se calcula mediante:

$$\rho = \frac{\lambda}{\mu}$$

Para que el sistema sea estable debe cumplirse:

$$\lambda < \mu$$

Las principales medidas teóricas del sistema son:

**Número promedio de clientes en el sistema:**

$$L = \frac{\lambda}{\mu - \lambda}$$

**Número promedio de clientes en la cola:**

$$L_q = \frac{\lambda^2}{\mu(\mu - \lambda)}$$

**Tiempo promedio que un cliente permanece en el sistema:**

$$W = \frac{1}{\mu - \lambda}$$

**Tiempo promedio que un cliente espera en la cola:**

$$W_q = \frac{\lambda}{\mu(\mu - \lambda)}$$

| Símbolo | Significado |
|---------|-------------|
| $\lambda$ | Tasa de llegada de clientes |
| $\mu$ | Tasa de servicio |
| $\rho$ | Utilización del servidor |
| $L$ | Número promedio de clientes en el sistema |
| $L_q$ | Número promedio de clientes en la cola |
| $W$ | Tiempo promedio en el sistema |
| $W_q$ | Tiempo promedio de espera en la cola |

### Escenarios de simulación

| Escenario | $\lambda$ | $\mu$ | Condición |
|-----------|-----------|-------|-----------|
| 1 | 2.0 | 3.0 | Sistema estable |
| 2 | 2.5 | 3.0 | Mayor utilización |
| 3 | 2.9 | 3.0 | Cerca de saturación |

Para cada escenario se registran:

- $\rho$, $L$, $L_q$, $W$ y $W_q$ (valores teóricos).
- Tiempo promedio de espera simulado ($W_{q,sim}$ y $W_{sim}$).
- Tamaño promedio de la cola simulado ($L_{q,sim}$ y $L_{sim}$).

Posteriormente, los resultados se comparan mediante una tabla que incluye el **porcentaje de error** entre lo teórico y lo simulado.

## 2. Tecnologías utilizadas

- **Lenguajes:** Python 3.x
- **Librerías:** `simpy` (simulación de eventos discretos), `numpy` (cálculos numéricos), `matplotlib` (gráficas y animaciones).

## 3. Instalación

### 3.1. Clonar el repositorio

```bash
git clone https://github.com/Dan-San837/Simulacion.git
cd Simulacion/Practica3
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
python -m src.main
# Linux y MacOS
python3 -m src.main
```

### 4.2. Ejemplos de uso

- Ejecutar los 3 escenarios con tabla comparativa, animación y gráficas: `python -m src.main`
- Modificar los escenarios en `src/models/m_model.py` (`construir_escenarios`) para analizar otros valores de $\lambda$ y $\mu$.
- Ajustar la duración de la simulación y la semilla en `src/controllers/m_controller.py` (`TIEMPO_SIMULACION`, `SEMILLA`).
- Omitir la animación llamando `mostrar_resultados(resultados, con_animacion=False)` en `src/main.py`.

## 5. Estructura del proyecto

| Carpeta/Archivo | Descripción |
|-----------------|-------------|
| `src/main.py` | Punto de entrada: construye los escenarios, ejecuta las simulaciones estables y muestra los resultados. |
| `src/models/m_model.py` | Estructuras de datos (`Escenario`, resultados teóricos/simulados) y cálculo teórico de ρ, L, Lq, W y Wq. |
| `src/controllers/m_controller.py` | Simulación con SimPy: generación de llegadas Poisson, servicio exponencial con servidor único FIFO y cálculo de métricas simuladas. |
| `src/views/m_view.py` | Tablas comparativas con error %, animación de la cola y gráficas de tamaño de cola y distribución de esperas. |
| `requirements.txt` | Dependencias de Python. |
| `README.md` | Documentación del proyecto. |

## 6. Información adicional

- **Funcionalidades:** Estimación teórica de las medidas del sistema M/M/1, simulación de eventos discretos con SimPy, comparación teórico vs. simulado con porcentaje de error, tabla resumen de escenarios, animación interactiva de llegadas/cola/atención y generación de gráficas (`graficas_mm1.png`).
- **Escenarios:** Sistema estable (ρ=0.667), mayor utilización (ρ=0.833) y cerca de saturación (ρ=0.967).
- **Capturas:**
    - Gráficas de cola y tiempos de espera por escenario
    <img width="580" height="500" alt="image" src="https://github.com/Dan-San837/Simulacion/blob/develop/docs/graficas_mm1.png?raw=true">

---

## Flujo del proyecto

1. **Definición:** Establece el objetivo, propósito y problema a resolver mediante el modelo de colas M/M/1.
2. **Tecnologías:** Identifica herramientas, lenguajes y librerías necesarias.
3. **Instalación:** Clonación, configuración de entorno virtual e instalación de dependencias con `pip install`.
4. **Uso:** Ejecución con `python -m src.main` para obtener tablas comparativas, animación y gráficas.
5. **Estructura:** Comprende la arquitectura MVC (modelo, controlador, vista), permite la incorporación de nuevos escenarios y facilita la validación teórica frente a la simulación.
