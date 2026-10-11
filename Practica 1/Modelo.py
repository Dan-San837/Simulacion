class ModeloAtmosferico:

    # Tabla de reglas del enunciado (temperatura en °C -> factor de temperatura Tf).
    # El primer y el último par son los puntos de saturación del modelo.
    TABLA_TF = (
        (10, 1.00),  # <= 10 °C
        (12, 0.90),
        (14, 0.80),
        (16, 0.70),
        (18, 0.60),
        (20, 0.50),
        (22, 0.40),
        (24, 0.30),
        (26, 0.20),
        (28, 0.10),  # >= 28 °C
    )

    @staticmethod
    def normalizar_porcentaje(valor: float) -> float:
        """Convierte un porcentaje (0-100) a decimal (0.0 - 1.0)."""
        return valor / 100.0

    @staticmethod
    def calcular_tf(temp: float) -> float:
        """Devuelve el factor de temperatura Tf a partir de la tabla de reglas.

        Las temperaturas que caen dentro de un tramo se resuelven con
        interpolación lineal entre los dos factores de sus extremos
        (p. ej. 17 °C -> 0.65, 15 °C -> 0.75) y fuera de la tabla el valor
        se satura en 1.00 (<= 10 °C) o 0.10 (>= 28 °C).
        """
        tabla = ModeloAtmosferico.TABLA_TF

        # Saturación por debajo del primer tramo de la tabla.
        if temp <= tabla[0][0]:
            return tabla[0][1]

        # Saturación por encima del último tramo de la tabla.
        if temp >= tabla[-1][0]:
            return tabla[-1][1]

        # Buscamos el tramo [t0, t1] que contiene la temperatura y aplicamos la
        # ecuación de la recta entre sus dos factores:
        #   Tf = f0 + (f1 - f0) * (temp - t0) / (t1 - t0)
        for (t0, f0), (t1, f1) in zip(tabla, tabla[1:]):
            if t0 <= temp <= t1:
                return f0 + (f1 - f0) * (temp - t0) / (t1 - t0)

        return tabla[-1][1]

    @staticmethod
    def calcular_indice(h: float, n: float, tf: float) -> float:
        """Índice de lluvia: I = 0.5*H + 0.3*N + 0.2*Tf (ponderación del enunciado)."""
        return 0.5 * h + 0.3 * n + 0.2 * tf
        # 0.1 * h + 0.3 * n + 0.6 * tf modelo ajustado como prueba 

    @staticmethod
    def clasificar_estado(I: float) -> str:
        if I < 0.40:
            return "Sin lluvia"
        elif 0.40 <= I < 0.60:
            return "Baja posibilidad"
        elif 0.60 <= I < 0.75:
            return "Lluvia probable"
        else:
            return "Lluvia"