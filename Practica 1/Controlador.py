from Modelo import ModeloAtmosferico

class ControladorAtmosferico:

    def __init__(self, datos_entrada):
        """
        datos_entrada es una lista de diccionarios con la estructura:
        [{'hora': '00:00', 'humedad': 82, 'nubosidad': 35, 'temp': 13}, ...]
        """
        self.datos_entrada = datos_entrada
        self.resultados = []

    def procesar_simulacion(self):
        self.resultados = []

        for registro in self.datos_entrada:
            hora = registro['hora']

            # H y N llegan como porcentaje y el Modelo trabaja en 0.0 - 1.0.
            H = ModeloAtmosferico.normalizar_porcentaje(registro['humedad'])
            N = ModeloAtmosferico.normalizar_porcentaje(registro['nubosidad'])

            # Tf se obtiene de la tabla de temperatura del enunciado. Solo si el
            # registro trae un 'Tf' explícito se respeta ese valor como ajuste manual.
            Tf = registro['Tf'] if 'Tf' in registro else ModeloAtmosferico.calcular_tf(registro['temp'])

            # Índice ponderado y clasificación según la tabla de reglas.
            I = ModeloAtmosferico.calcular_indice(H, N, Tf)
            estado = ModeloAtmosferico.clasificar_estado(I)

            self.resultados.append({
                'hora': hora,
                'humedad': registro['humedad'],
                'nubosidad': registro['nubosidad'],
                'temp': registro['temp'],
                'H': round(H, 2),
                'N': round(N, 2),
                'Tf': round(Tf, 2),
                'indice': round(I, 3),
                'estado': estado
            })

        return self.resultados