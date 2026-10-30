# Generador Congruencial Lineal

## Descripción

Este proyecto implementa un Generador Congruencial Lineal (GCL) en Python y Java. Ambas versiones producen una secuencia reproducible con los mismos parámetros, guardan los resultados en CSV, analizan el periodo y generan una gráfica de los valores normalizados.

La relación recursiva es:

$$X_{n+1} = (a \cdot X_n + c) \bmod m$$

Cada estado se normaliza mediante:

$$U_n = \frac{X_n}{m}$$

Los valores usados por defecto son:

| Parámetro | Valor |
|---|---:|
| Multiplicador $a$ | 1664525 |
| Incremento $c$ | 1013904223 |
| Módulo $m$ | $2^{32}$ (4294967296) |
| Semilla $X_0$ | 12345 |
| Cantidad de estados | 10000 |

La semilla es el estado inicial $X_0$; la secuencia almacenada comienza con el resultado de aplicar la recurrencia una vez, $X_1$. La calidad y el periodo del generador dependen de sus parámetros y de la semilla. El programa comprueba las condiciones de periodo completo de Hull-Dobell para los parámetros configurados.

## Metodología

1. Inicializar el generador con $a=1664525$, $c=1013904223$, $m=2^{32}$ y $X_0=12345$.
2. Calcular 10000 estados consecutivos en Python y normalizarlos dividiendo cada estado entre $m$.
3. Implementar el mismo procedimiento en Java con los mismos parámetros.
4. Guardar y comparar los estados y valores normalizados de las primeras 20 observaciones.
5. Graficar las primeras 1000 observaciones como secuencia y las 10000 observaciones como dispersión.

El resultado esperado es que ambas implementaciones produzcan los mismos estados y valores normalizados para una misma configuración. La comparación automática verifica los CSV de las primeras 20 observaciones.

## Tecnologías

- Python 3 y `matplotlib` (versión 3.7.0 o posterior), declarada en `Python/requirements.txt`.
- JDK 8 o posterior, que incluye `javac`, `java`, Java2D e `ImageIO`. Java no requiere bibliotecas externas.

## Instalación

### Requisitos

- Python 3 instalado y disponible como `python3` (o `python` en Windows).
- JDK 8 o posterior instalado; los comandos `java` y `javac` deben estar disponibles en la terminal.
- No se necesita instalar Git si ya se cuenta con la carpeta del proyecto.

### Preparar Python

Desde la carpeta raíz del proyecto:

```bash
cd Python
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
```

En Windows, activar el entorno con `.venv\Scripts\activate` y usar `python` en lugar de `python3` si ese es el comando instalado.

### Preparar Java

No hay dependencias Java externas. Verifique la instalación del JDK:

```bash
java -version
javac -version
```

## Uso

### Ejecutar Python

Desde la raíz del proyecto, active el entorno virtual si se creó durante la instalación y ejecute:

```bash
cd Python
python3 main.py
```

En Windows, use `python main.py` si corresponde. Python genera `python_secuencia.csv`, `python_primeras20.csv` y `python_secuencia.png` dentro de `Python/resultados/`. También compara las primeras 20 observaciones con el CSV de Java si ya existe.

### Ejecutar Java

Desde la raíz del proyecto:

```bash
cd Java
javac *.java
java main
```

Java genera `java_secuencia.csv`, `java_primeras20.csv` y `java_secuencia.png` dentro de `Java/resultados/`. También compara las primeras 20 observaciones con el CSV de Python si ya existe.

### Comparar ambas implementaciones

Para que ambas ejecuciones encuentren el CSV de la otra versión, ejecute Python y después Java:

```bash
cd Python
python3 main.py
cd ../Java
javac *.java
java main
```

La ejecución de Java confirma la comparación con el CSV recién generado por Python. Si desea que Python muestre también la comparación con Java, vuelva a ejecutar `python3 main.py` después de ejecutar Java.

## Resultados

Los archivos de salida se crean en la carpeta `resultados` de cada lenguaje:

| Archivo | Contenido |
|---|---|
| `*_secuencia.csv` | Las 10000 observaciones, con columnas `n`, `X` y `U`. |
| `*_primeras20.csv` | Las primeras 20 observaciones, con columnas `n`, `X` y `U`. |
| `*_secuencia.png` | Gráfica de la secuencia normalizada. |

## Estructura del proyecto

```text
Practica5/
├── README.md
├── Java/
│   ├── controller.java
│   ├── main.java
│   ├── model.java
│   ├── view.java
│   └── resultados/
│       └── ejecute el proyecto 
│        
└── Python/
    ├── controller.py
    ├── main.py
    ├── model.py
    ├── requirements.txt
    ├── view.py
    └── resultados/
        └── ejecute el proyecto
```


