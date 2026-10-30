import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class controller {
    private static final int CANTIDAD = 10000;
    private static final int PRIMERAS = 20;

    private final Path directorioResultados;
    private final model modelo;
    private final view vista;

    public controller() {
        Path directorioBase = obtenerDirectorioBase();
        this.directorioResultados = directorioBase.resolve("resultados");
        this.modelo = new model();
        this.vista = new view(this.directorioResultados);
    }

    public void ejecutar() throws IOException {
        this.modelo.reiniciar();
        this.vista.mostrarConfiguracion(this.modelo, CANTIDAD);

        long[] estados = this.modelo.generar(CANTIDAD);
        double[] normalizados = this.modelo.normalizar(estados);
        long[] estadosIniciales = Arrays.copyOf(estados, PRIMERAS);
        double[] normalizadosIniciales = Arrays.copyOf(normalizados, PRIMERAS);

        this.vista.mostrarPrimeras(
            estadosIniciales,
            normalizadosIniciales,
            "Primeras 20 observaciones"
        );

        Path rutaCsv = this.vista.guardarSecuenciaCsv(
            estados,
            normalizados,
            "java_secuencia.csv"
        );
        Path rutaPrimeras = this.vista.guardarSecuenciaCsv(
            estadosIniciales,
            normalizadosIniciales,
            "java_primeras20.csv"
        );
        Path rutaGrafica = this.vista.graficarSecuencia(
            normalizados,
            "java_secuencia.png"
        );

        System.out.println();
        System.out.println("Archivos generados:");
        System.out.println("  - " + rutaCsv);
        System.out.println("  - " + rutaPrimeras);
        System.out.println("  - " + rutaGrafica);

        model.Periodo periodo = this.modelo.analizarPeriodo(estados);
        this.vista.mostrarPeriodo(periodo);

        compararConPython(estadosIniciales, normalizadosIniciales);
    }

    private void compararConPython(long[] estados, double[] normalizados)
        throws IOException {
        Path rutaPython = this.directorioResultados
            .getParent()
            .getParent()
            .resolve("Python")
            .resolve("resultados")
            .resolve("python_primeras20.csv")
            .normalize();

        if (!Files.exists(rutaPython)) {
            this.vista.mostrarComparacion(null, false, rutaPython);
            return;
        }

        List<String> lineas = Files.readAllLines(rutaPython, StandardCharsets.UTF_8);
        List<view.FilaComparacion> filas = new ArrayList<>();
        for (int i = 1; i < lineas.size() && filas.size() < estados.length; i++) {
            String[] partes = lineas.get(i).split(",");
            if (partes.length < 3) {
                continue;
            }
            long xPython = Long.parseLong(partes[1].trim());
            double uPython = Double.parseDouble(partes[2].trim());
            int indice = filas.size();
            boolean igual =
                xPython == estados[indice]
                    && Math.abs(uPython - normalizados[indice]) <= 1e-15;
            filas.add(
                new view.FilaComparacion(
                    indice + 1,
                    xPython,
                    estados[indice],
                    uPython,
                    normalizados[indice],
                    igual
                )
            );
        }

        boolean coinciden = filas.size() == estados.length;
        for (view.FilaComparacion fila : filas) {
            if (!fila.igual) {
                coinciden = false;
                break;
            }
        }
        this.vista.mostrarComparacion(filas, coinciden, rutaPython);
    }

    private static Path obtenerDirectorioBase() {
        try {
            Path ubicacion = Paths.get(
                controller.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
            );
            if (Files.isDirectory(ubicacion)) {
                return ubicacion;
            }
            return ubicacion.getParent();
        } catch (Exception excepcion) {
            return Paths.get("").toAbsolutePath();
        }
    }
}
