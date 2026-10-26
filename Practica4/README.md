# Práctica 4 - Modelo SIR en Java

Proyecto de simulación epidemiológica basado en el modelo SIR (Susceptible, Infectado, Recuperado). La aplicación está desarrollada en Java y se ejecuta desde la carpeta raíz del proyecto `Practica4/`.

> Este repositorio se trabaja desde la carpeta `Practica4`, y los ejemplos y rutas de salida están adaptados a ese nombre.

La aplicación permite ejecutar la simulación, visualizar la evolución en consola, exportar los resultados a CSV y generar una gráfica de los estados a lo largo del tiempo.

## Descripción

El proyecto simula una población cerrada en pasos discretos, donde cada individuo puede pasar por los estados:

- Susceptible (S)
- Infectado (I)
- Recuperado (R)

La lógica principal está en el paquete `sir`, y el flujo puede resumirse como:

1. se cargan los parámetros del modelo
2. se genera la población inicial
3. se ejecutan los pasos de la simulación
4. se exporta el resultado a CSV
5. se genera una gráfica y, opcionalmente, se muestra la interfaz

## Estructura del proyecto

```text
.
├── src/
│   └── sir/
│       ├── Main.java
│       ├── controller/
│       │   └── ControladorSIR.java
│       ├── model/
│       │   ├── Estado.java
│       │   ├── Individuo.java
│       │   ├── ParametrosSIR.java
│       │   ├── ResultadoPaso.java
│       │   └── SimulacionSIR.java
│       └── view/
│           ├── GraficaSIR.java
│           └── VistaConsola.java
├── NetLogo/
│   ├── modelo_sir_netlogo.nlogo
│   └── modelo_sir_netlogo.nlogox
├── pom.xml
├── requirements.txt
└── README.md
```

## Tecnologías utilizadas

- Java 11+ (actualmente compatible con el proyecto y con `pom.xml`)
- Maven para compilar y empaquetar
- Swing/AWT para la gráfica y vista local
- NetLogo para visualización y comparación de resultados opcional
- CSV para exportar la evolución temporal

## Requisitos previos

- JDK instalado y configurado en `PATH`
- Maven (opcional si se usa el `pom.xml` directamente)
- NetLogo 7.x opcional, solo si quieres abrir la visualización del modelo

## Compilar el proyecto

Desde la raíz del proyecto (`Practica4/`):

```bash
cd Practica4
mvn clean package
```

También puedes compilar sin empaquetar:

```bash
cd Practica4
mvn clean compile
```

## Ejecutar la simulación

### Opción 1: con Java directo

Desde `Practica4/`:

```bash
cd Practica4
java -cp out sir.Main
```

### Opción 2: con el JAR generado

```bash
cd Practica4
java -jar target/sir-simulacion.jar
```

### Opciones disponibles

```bash
cd Practica4
java -cp out sir.Main --semilla 42
java -cp out sir.Main --csv NetLogo/resultados_java.csv
java -cp out sir.Main --sin-grafica
java -cp out sir.Main --ayuda
```

Parámetros principales:

- `--semilla <numero>`: fija la semilla aleatoria
- `--csv <ruta>`: define la ruta del archivo CSV de salida
- `--sin-grafica`: ejecuta la simulación sin abrir la ventana gráfica
- `--ayuda` o `-h`: muestra la ayuda del programa

## Parámetros por defecto del modelo

El modelo usa estos valores iniciales por defecto:

- Población: 100
- Infectados iniciales: 5
- Beta: 0.30
- Gamma: 0.10
- Tiempo de simulación: 100
- Contactos por infectado: 1

La fórmula del número reproductivo nominal base se calcula como:

```text
R0 = beta / gamma
```

## Salidas generadas

La ejecución genera, por defecto, los siguientes artefactos:

- `NetLogo/resultados_java.csv`: archivo con la evolución de S, I y R
- `grafica_sir_java.png`: imagen con la gráfica del modelo
- salida en consola con el resumen final y el pico de infectados

Formato del CSV:

```csv
tiempo,susceptibles,infectados,recuperados
0,95,5,0
1,93,7,0
```

## Visualización con NetLogo

La carpeta `NetLogo/` contiene los archivos del modelo para visualización. Si deseas comparar la salida Java con la representación del modelo en NetLogo, puedes abrir los archivos `.nlogo` o `.nlogox` desde NetLogo y cargar el CSV generado por la simulación.

## Observaciones

- El proyecto no usa dependencias externas de Python ni librerías de terceros para la lógica principal.
- El archivo `requirements.txt` no es requerido para ejecutar este proyecto Java.
- El resultado del programa queda guardado en la carpeta `NetLogo/` por defecto, lo que facilita la integración con NetLogo.

## Ejemplo de ejecución completa

```bash
mvn clean package
java -jar target/sir-simulacion.jar --semilla 42 --sin-grafica
```

Esto genera la simulación con una semilla fija y sin abrir la interfaz gráfica.

## Autoría y contexto

Este repositorio corresponde a la práctica 4 del modelo SIR en Java, con integración opcional a NetLogo para la representación visual de la evolución de la epidemia.
