import matplotlib.pyplot as plt
import numpy as np

from Controlador import ControladorAtmosferico

# Datos de la jornada: cada registro es una hora medida (hora, humedad %, nubosidad %, temp °C).
datos_tabla = [
    {'hora': '06:00', 'humedad': 65, 'nubosidad': 40, 'temp': 14},
    {'hora': '08:00', 'humedad': 70, 'nubosidad': 50, 'temp': 16},
    {'hora': '10:00', 'humedad': 68, 'nubosidad': 45, 'temp': 18},
    {'hora': '12:00', 'humedad': 60, 'nubosidad': 30, 'temp': 22},
    {'hora': '14:00', 'humedad': 75, 'nubosidad': 70, 'temp': 20},
    {'hora': '16:00', 'humedad': 85, 'nubosidad': 85, 'temp': 18},
    {'hora': '18:00', 'humedad': 92, 'nubosidad': 95, 'temp': 16},
    {'hora': '20:00', 'humedad': 88, 'nubosidad': 90, 'temp': 17},
    {'hora': '22:00', 'humedad': 80, 'nubosidad': 75, 'temp': 15},
]

def mostrar_tabla_consola(resultados):
    """Imprime los datos medidos junto a sus versiones normalizadas y el índice I."""
    print(f"\n{'Hora':<7}{'Hum.':>6}{'Nub.':>6}{'Temp.':>7}{'H':>7}{'N':>7}{'Tf':>7}{'Indice':>9}")
    print("-" * 56)
    for r in resultados:
        print(f"{r['hora']:<7}{r['humedad']:>6}{r['nubosidad']:>6}{r['temp']:>7}"
              f"{r['H']:>7.2f}{r['N']:>7.2f}{r['Tf']:>7.2f}{r['indice']:>9.3f}")

def calcular_correlaciones(resultados):
    """Coeficiente r de Pearson entre cada variable de entrada (H, N, Tf) e I.

    Sirve para identificar cuál de las tres variables tiene más influencia sobre el
    índice de lluvia. No imprime nada: es un cálculo puro.
    """
    # I es la variable dependiente: sobre ella se mide el grado de asociación.
    indices = np.array([r['indice'] for r in resultados])
    correlaciones = {}

    for clave in ('H', 'N', 'Tf'):
        valores = np.array([r[clave] for r in resultados])
        # r no está definido si alguna de las dos series no varía (desviación 0).
        if valores.std() == 0 or indices.std() == 0:
            correlaciones[clave] = 0.0
        else:
            # np.corrcoef devuelve la matriz de correlación; [0, 1] es el valor con I.
            correlaciones[clave] = float(np.corrcoef(valores, indices)[0, 1])

    return correlaciones

def graficar_resultados(resultados):
    """Gráficas de crecimiento y decrecimiento de las variables y del índice."""
    horas = [r['hora'] for r in resultados]

    fig, (ax_vars, ax_indice) = plt.subplots(2, 1, figsize=(12, 9), sharex=True)

    # Panel 1: las cuatro series normalizadas en la misma escala para comparar
    # crechas y decrecimientos.
    series = (
        ('H', 'Humedad (H)', 'tab:blue'),
        ('N', 'Nubosidad (N)', 'tab:gray'),
        ('Tf', 'Factor temperatura (Tf)', 'tab:red'),
        ('indice', 'Indice de lluvia (I)', 'tab:green'),
    )
    for clave, etiqueta, color in series:
        ax_vars.plot(horas, [r[clave] for r in resultados], label=etiqueta,
                     marker='o', markersize=3, color=color, linewidth=1.6)

    ax_vars.set_title('Variables normalizadas e indice I (escala 0.0 - 1.0)')
    ax_vars.set_ylabel('Valor normalizado')
    ax_vars.set_ylim(-0.05, 1.05)
    ax_vars.grid(True, alpha=0.3)
    ax_vars.legend(loc='lower left', ncol=4, fontsize=8)

    # Panel 2: el índice con los tres umbrales de la tabla de reglas.
    valores = [r['indice'] for r in resultados]
    ax_indice.plot(horas, valores, color='tab:green', marker='o', markersize=4,
                   linewidth=1.8, label='Indice de lluvia (I)', zorder=3)

    umbrales = (
        (0.75, 'Lluvia (I >= 0.75)', 'tab:red'),
        (0.60, 'Lluvia probable (I >= 0.60)', 'tab:orange'),
        (0.40, 'Baja posibilidad (I >= 0.40)', 'tab:olive'),
    )
    for umbral, etiqueta, color in umbrales:
        ax_indice.axhline(y=umbral, linestyle='--', color=color, linewidth=1.2, label=etiqueta)

    # Banda sombreada donde el modelo ya considera lluvia, y marcado de las horas exactas.
    ax_indice.fill_between(range(len(horas)), 0.75, 1.0, color='tab:blue', alpha=0.08)
    horas_lluvia = [r['hora'] for r in resultados if r['indice'] >= 0.75]
    if horas_lluvia:
        ax_indice.scatter(
            [h for h in horas_lluvia], [r['indice'] for r in resultados if r['indice'] >= 0.75],
            s=70, facecolor='none', edgecolor='tab:blue', linewidths=2,
            label='Horas con lluvia', zorder=4,
        )

    ax_indice.set_title('Indice de lluvia con los umbrales de la tabla de reglas')
    ax_indice.set_ylabel('Indice I')
    ax_indice.set_xlabel('Hora del dia')
    ax_indice.set_ylim(-0.05, 1.05)
    ax_indice.grid(True, alpha=0.3)
    ax_indice.legend(loc='upper left', fontsize=8)

    plt.setp(ax_indice.get_xticklabels(), rotation=45, fontsize=9)
    fig.tight_layout()

    plt.show()

if __name__ == "__main__":
    # Inicializar controlador y ejecutar la simulación.
    controlador = ControladorAtmosferico(datos_tabla)
    resultados = controlador.procesar_simulacion()

    mostrar_tabla_consola(resultados)
    graficar_resultados(resultados)