# Generación y análisis de números pseudoaleatorios (Práctica 6)

## 1. Descripción del proyecto

- **Objetivo:** Generar números pseudoaleatorios con un generador congruencial lineal (LCG) y evaluar si su distribución es compatible con una distribución uniforme en el intervalo \([0,1]\).
- **Propósito:** Aplicar las pruebas Chi-cuadrado y Kolmogorov-Smirnov, interpretar sus estadísticos y valores p, y comparar distintas configuraciones del generador.
- **Problema resuelto:** Automatizar la generación de muestras, el análisis estadístico y la presentación de resultados con implementaciones equivalentes en Python y Java.

### Fundamento teórico

Los números pseudoaleatorios se obtienen mediante algoritmos deterministas que producen secuencias con propiedades estadísticas similares a las de valores aleatorios. Para esta práctica se evalúa su uniformidad en \([0,1]\), usando las hipótesis:

- \(H_0: X \sim U(0,1)\): la secuencia es compatible con una distribución uniforme.
- \(H_1: X \not\sim U(0,1)\): la secuencia no es compatible con una distribución uniforme.

El nivel de significancia predeterminado es \(\alpha = 0.05\). Si el valor p es mayor o igual que \(\alpha\), no se rechaza \(H_0\); si es menor, se rechaza \(H_0\).

### Métodos utilizados

#### Generador congruencial lineal (LCG)

El LCG genera la secuencia mediante la recurrencia:

$$
X_{n+1} = (aX_n + c) \bmod m
$$

Cada estado se normaliza para obtener un valor en \([0,1)\):

$$
U_n = \frac{X_n}{m}
$$

Donde:

* \(X_n\): semilla o estado actual.
* \(a\): multiplicador.
* \(c\): incremento.
* \(m\): módulo.
* \(X_{n+1}\): nuevo estado generado.

#### Prueba Chi-cuadrado (\(\chi^2\))

Divide el intervalo \([0,1]\) en \(k\) subintervalos y compara las frecuencias observadas con las esperadas:

$$
\chi^2 = \sum_{i=1}^{k} \frac{(O_i-E_i)^2}{E_i}
$$

Donde \(O_i\) es la frecuencia observada y \(E_i=N/k\) la frecuencia esperada para una muestra de tamaño \(N\). Los grados de libertad son \(k-1\); con los 10 intervalos predeterminados son 9.

#### Prueba Kolmogorov-Smirnov (KS)

Compara la función de distribución acumulada empírica de la muestra con la distribución uniforme teórica:

$$
D = \sup_x \left|F_n(x)-F(x)\right|
$$

Donde \(F_n(x)\) es la distribución acumulada empírica y \(F(x)\) la distribución acumulada teórica. El programa presenta el estadístico \(D\), sus diferencias positiva y negativa, un valor p aproximado y una gráfica opcional de ambas distribuciones.

#### Estimación de \(\pi\) por Monte Carlo

Como experimento complementario, la implementación usa pares de valores generados por el LCG para simular puntos en el cuadrado unidad y estimar \(\pi\) a partir de la proporción que cae dentro del cuarto de círculo. El benchmark compara la estimación, el error absoluto y el tiempo para distintos tamaños de muestra.

## 2. Tecnologías utilizadas

- **Lenguajes:** Python 3.10 o superior y Java (JDK 17 o superior).
- **Librerías de Python:** `matplotlib` para guardar la gráfica de la distribución acumulada. Las pruebas estadísticas y el LCG están implementados en el proyecto.
- **Java:** utiliza las bibliotecas estándar; la gráfica se guarda como SVG.

## 3. Instalación

Ejecuta los comandos desde la carpeta raíz de la práctica.

### 3.1. Python

```bash
cd Python

# Crear y activar un entorno virtual (recomendado)
python3 -m venv .venv
source .venv/bin/activate

# Windows PowerShell:
# .venv\Scripts\Activate.ps1

# Instalar dependencias
python -m pip install -r requirements.txt
```

### 3.2. Java

Comprueba que JDK 17 o superior esté instalado. No se requieren dependencias externas.

## 4. Uso

### 4.1. Ejecutar en Python

Desde la carpeta `Python`:

```bash
python main.py
```

La ejecución predeterminada genera 10 000 valores, aplica ambas pruebas y estima \(\pi\). Para consultar todas las opciones:

```bash
python main.py --help
```

Ejemplos:

```bash
# Ejecutar el benchmark de estimación de π
python main.py --benchmark

# Comparar configuraciones y guardar la secuencia y la gráfica
python main.py --n 10000 --compare-configurations --csv secuencia.csv --plot distribucion.png
```

### 4.2. Ejecutar en Java

Desde la carpeta `Java`:

```bash
# Compilar; los archivos .class se guardan en out/
mkdir -p out
javac -d out *.java models/*.java

# Ejecutar
java -cp out main
```

Para consultar todas las opciones:

```bash
java -cp out main --help
```

Ejemplos:

```bash
# Ejecutar el benchmark de estimación de π
java -cp out main --benchmark

# Comparar configuraciones y guardar la secuencia y la gráfica SVG
java -cp out main --n 10000 --compare-configurations --csv secuencia.csv --plot distribucion.svg
```

### Opciones principales

Ambas implementaciones aceptan opciones equivalentes:

| Opción | Descripción |
|--------|-------------|
| `--n` | Cantidad de valores a generar (predeterminado: 10 000). |
| `--seed` | Semilla inicial del LCG. |
| `--a` | Multiplicador del LCG. |
| `--c` | Incremento del LCG. |
| `--m` | Módulo del LCG. |
| `--intervals` | Número de intervalos de la prueba Chi-cuadrado (predeterminado: 10). |
| `--alpha` | Nivel de significancia (predeterminado: 0.05). |
| `--benchmark` | Ejecuta el benchmark de estimación de \(\pi\). |
| `--compare-configurations` | Compara el LCG predeterminado, Park-Miller y un LCG de período corto. |
| `--show-sequence` | Imprime todos los valores generados; por defecto se muestra una vista previa. |
| `--csv` | Guarda la secuencia generada en un archivo CSV. |
| `--plot` | Guarda la comparación entre la distribución empírica y la uniforme teórica. |

Para comparar las secuencias de Python y Java, ejecútalos con los mismos valores de `--n`, `--seed`, `--a`, `--c` y `--m`. El tamaño de muestra debe ser al menos cinco veces el número de intervalos de Chi-cuadrado.

## 5. Estructura del proyecto

| Carpeta/Archivo | Descripción |
|-----------------|-------------|
| `Python/main.py` | Punto de entrada de la implementación Python. |
| `Python/controller.py` | Configura la ejecución, procesa argumentos y coordina generación, pruebas y benchmark. |
| `Python/view.py` | Presenta los resultados y genera salidas CSV y gráficas. |
| `Python/models/lcg.py` | Implementa el generador congruencial lineal. |
| `Python/models/chi_cuadrado.py` | Implementa la prueba Chi-cuadrado. |
| `Python/models/Kolmogorov.py` | Implementa la prueba Kolmogorov-Smirnov. |
| `Python/models/monte_carlo.py` | Implementa la estimación de \(\pi\) por Monte Carlo. |
| `Python/requirements.txt` | Dependencias de Python para generar gráficas. |
| `Java/main.java` | Punto de entrada de la implementación Java. |
| `Java/controller.java` | Procesa opciones y coordina generación, pruebas y benchmark. |
| `Java/view.java` | Presenta los resultados y genera salidas CSV y gráficas SVG. |
| `Java/models/LCG.java` | Implementa el generador congruencial lineal. |
| `Java/models/ChiCuadrado.java` | Implementa la prueba Chi-cuadrado. |
| `Java/models/Kolmogorov.java` | Implementa la prueba Kolmogorov-Smirnov. |
| `Java/models/MonteCarloPi.java` | Implementa la estimación de \(\pi\) por Monte Carlo. |
| `Java/out/` | Directorio de salida para las clases compiladas de Java. |
| `Teoria.md` | Objetivos, fundamentos teóricos y algoritmos de la práctica. |
| `Ejemplo.md` | Referencia de estructura para este README. |
| `README.md` | Documentación de la práctica. |

## 6. Información adicional

- **Funcionalidades:** generación reproducible mediante LCG; pruebas de uniformidad Chi-cuadrado y Kolmogorov-Smirnov; interpretación de valores p; comparación de configuraciones; estimación y benchmark de \(\pi\); exportación opcional de la secuencia a CSV y de la distribución acumulada a una gráfica.
- **Resultados esperados:** ambas implementaciones producen la misma secuencia cuando reciben los mismos parámetros. Los resultados de las pruebas permiten evaluar si se rechaza la hipótesis de uniformidad; la estimación de \(\pi\) y su tiempo se muestran para los tamaños 1 000, 10 000, 100 000 y 1 000 000.

### 6.1. Declaraciòn de uso de IA
**Herramienta utilizada:** OpenCode

**Forma de Uso:** La herramienta fue utilizada como apoyo para revisar, corregir e identificar errores en la estructura del còdigo desarrollado.

**Alcance de su intervenciòn:** Medio

## Flujo del proyecto

1. **Configuración:** selecciona el tamaño de la muestra, la semilla y los parámetros del LCG.
2. **Generación:** obtiene y normaliza los estados del generador para formar la secuencia en \([0,1)\).
3. **Evaluación:** aplica Chi-cuadrado y Kolmogorov-Smirnov, y compara sus valores p con \(\alpha\).
4. **Análisis complementario:** estima \(\pi\) mediante Monte Carlo o compara distintas configuraciones del LCG.
5. **Presentación:** muestra los resultados y, si se solicita, guarda la secuencia CSV y la gráfica de distribución acumulada.
