from Controlador import ControladorSimulacion

datos = [1000, 1100, 1250, 1400, 1600, 1850, 2100]


def main() -> None:
    controlador = ControladorSimulacion(datos)
    resultados = controlador.ejecutar()
    controlador.graficar(resultados)


if __name__ == "__main__":
    main()
