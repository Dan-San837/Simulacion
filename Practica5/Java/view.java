import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;

public class view {
    private final Path directorioResultados;

    public view(Path directorioResultados) {
        this.directorioResultados = directorioResultados;
    }

    public void mostrarConfiguracion(model modelo, int cantidad) {
        System.out.println(linea('=', 68));
        System.out.println("GENERADOR CONGRUENCIAL LINEAL");
        System.out.println(linea('=', 68));
        System.out.println("a       = " + modelo.a());
        System.out.println("c       = " + modelo.c());
        System.out.println("m       = " + modelo.m() + " (2^32)");
        System.out.println("semilla = " + modelo.semilla());
        System.out.println("valores = " + cantidad);
        System.out.println(linea('=', 68));
    }

    public void mostrarPrimeras(long[] estados, double[] normalizados, String titulo) {
        System.out.println();
        System.out.println(titulo + ":");
        System.out.println(String.format(Locale.US, "%5s %14s %16s", "n", "X(n)", "U(n)"));
        System.out.println(linea('-', 39));
        for (int i = 0; i < estados.length; i++) {
            System.out.println(
                String.format(
                    Locale.US,
                    "%5d %14d %16.12f",
                    i + 1,
                    estados[i],
                    normalizados[i]
                )
            );
        }
    }

    public void mostrarPeriodo(model.Periodo periodo) {
        System.out.println();
        System.out.println(linea('-', 68));
        System.out.println("ANALISIS DEL PERIODO");
        System.out.println(linea('-', 68));
        System.out.print("Factores primos de m: [");
        for (int i = 0; i < periodo.factoresPrimosM.length; i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(periodo.factoresPrimosM[i]);
        }
        System.out.println("]");
        System.out.println("Condiciones de Hull-Dobell:");
        System.out.println(
            "  - mcd(c, m) = 1: " + (periodo.mcdCumple ? "cumple" : "no cumple")
        );
        System.out.println(
            "  - (a - 1) divisible por los factores primos de m: "
                + (periodo.factoresCumplen ? "cumple" : "no cumple")
        );
        System.out.println(
            "  - (a - 1) divisible por 4 cuando m es multiplo de 4: "
                + (periodo.condicionCuatroCumple ? "cumple" : "no cumple")
        );
        if (periodo.periodoCompleto) {
            System.out.println(
                "Periodo teorico: " + periodo.periodoTeorico + " (periodo completo)"
            );
        } else {
            System.out.println(
                "No se puede garantizar el periodo completo con estos parametros."
            );
        }
        if (periodo.repetidosEnMuestra) {
            System.out.println(
                "Se encontraron valores repetidos en la muestra de " + periodo.muestra + "."
            );
        } else {
            System.out.println(
                "No hay valores repetidos en la muestra de " + periodo.muestra + ";"
            );
            System.out.println("esto es consistente con un periodo de 2^32.");
        }
    }

    public Path guardarSecuenciaCsv(
        long[] estados,
        double[] normalizados,
        String nombreArchivo
    ) throws IOException {
        Files.createDirectories(this.directorioResultados);
        Path ruta = this.directorioResultados.resolve(nombreArchivo);
        try (BufferedWriter escritor = Files.newBufferedWriter(
            ruta,
            StandardCharsets.UTF_8
        )) {
            escritor.write("n,X,U");
            escritor.newLine();
            for (int i = 0; i < estados.length; i++) {
                escritor.write(
                    String.format(
                        Locale.US,
                        "%d,%d,%.17g",
                        i + 1,
                        estados[i],
                        normalizados[i]
                    )
                );
                escritor.newLine();
            }
        }
        return ruta;
    }

    public Path graficarSecuencia(double[] normalizados, String nombreArchivo)
        throws IOException {
        Files.createDirectories(this.directorioResultados);
        Path ruta = this.directorioResultados.resolve(nombreArchivo);

        int ancho = 1200;
        int alto = 850;
        BufferedImage imagen = new BufferedImage(
            ancho,
            alto,
            BufferedImage.TYPE_INT_RGB
        );
        Graphics2D grafico = imagen.createGraphics();
        grafico.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        grafico.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        grafico.setColor(Color.WHITE);
        grafico.fillRect(0, 0, ancho, alto);

        grafico.setColor(Color.BLACK);
        grafico.setFont(new Font("SansSerif", Font.BOLD, 18));
        grafico.drawString(
            "Generador Congruencial Lineal - Secuencia normalizada U(n)",
            60,
            32
        );

        int cantidadMuestra = Math.min(1000, normalizados.length);
        double[] muestra = new double[cantidadMuestra];
        System.arraycopy(normalizados, 0, muestra, 0, cantidadMuestra);

        dibujarPanel(
            grafico,
            30,
            55,
            ancho - 60,
            340,
            muestra,
            "Primeras " + cantidadMuestra + " observaciones",
            true
        );
        dibujarPanel(
            grafico,
            30,
            450,
            ancho - 60,
            340,
            normalizados,
            "Secuencia completa (" + normalizados.length + " observaciones)",
            false
        );

        grafico.dispose();
        ImageIO.write(imagen, "png", ruta.toFile());
        return ruta;
    }

    public void mostrarComparacion(
        List<FilaComparacion> filas,
        boolean coinciden,
        Path rutaPython
    ) {
        System.out.println();
        System.out.println(linea('-', 68));
        System.out.println("COMPARACION CON PYTHON (primeras 20 observaciones)");
        System.out.println(linea('-', 68));
        if (filas == null || filas.isEmpty()) {
            System.out.println("No se encontro el archivo: " + rutaPython);
            System.out.println(
                "Ejecute la implementacion Python y vuelva a correr Java para comparar."
            );
            return;
        }

        System.out.println(
            String.format(
                Locale.US,
                "%5s %14s %14s %16s %16s %7s",
                "n",
                "X Python",
                "X Java",
                "U Python",
                "U Java",
                "Igual"
            )
        );
        System.out.println(linea('-', 81));
        for (FilaComparacion fila : filas) {
            System.out.println(
                String.format(
                    Locale.US,
                    "%5d %14d %14d %16.12f %16.12f %7s",
                    fila.n,
                    fila.xPython,
                    fila.xJava,
                    fila.uPython,
                    fila.uJava,
                    fila.igual ? "si" : "no"
                )
            );
        }
        if (coinciden) {
            System.out.println();
            System.out.println(
                "Resultado: las " + filas.size() + " observaciones coinciden."
            );
        } else {
            System.out.println();
            System.out.println(
                "Resultado: se encontraron diferencias entre las implementaciones."
            );
        }
    }

    private void dibujarPanel(
        Graphics2D grafico,
        int x,
        int y,
        int ancho,
        int alto,
        double[] valores,
        String titulo,
        boolean dibujarLineas
    ) {
        int izquierda = x + 70;
        int derecha = x + ancho - 30;
        int arriba = y + 40;
        int abajo = y + alto - 45;
        int anchoPlot = derecha - izquierda;
        int altoPlot = abajo - arriba;

        grafico.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int i = 0; i <= 4; i++) {
            double valor = i / 4.0;
            int py = abajo - (int) Math.round(valor * altoPlot);
            grafico.setColor(new Color(225, 225, 225));
            grafico.drawLine(izquierda, py, derecha, py);
            grafico.setColor(Color.DARK_GRAY);
            grafico.drawString(
                String.format(Locale.US, "%.2f", valor),
                izquierda - 45,
                py + 4
            );
        }

        grafico.setColor(Color.DARK_GRAY);
        grafico.drawLine(izquierda, arriba, izquierda, abajo);
        grafico.drawLine(izquierda, abajo, derecha, abajo);

        grafico.setColor(Color.BLACK);
        grafico.setFont(new Font("SansSerif", Font.BOLD, 14));
        grafico.drawString(titulo, izquierda, y + 22);
        grafico.setFont(new Font("SansSerif", Font.PLAIN, 12));
        grafico.drawString("n", derecha, abajo + 25);
        grafico.drawString("U(n)", x + 15, arriba - 12);

        int cantidad = valores.length;
        if (cantidad == 0) {
            return;
        }

        if (dibujarLineas) {
            grafico.setColor(new Color(31, 119, 180));
            int anteriorX = izquierda;
            int anteriorY = coordenadaY(valores[0], arriba, altoPlot);
            for (int i = 1; i < cantidad; i++) {
                int px = coordenadaX(i, cantidad, izquierda, anchoPlot);
                int py = coordenadaY(valores[i], arriba, altoPlot);
                grafico.drawLine(anteriorX, anteriorY, px, py);
                anteriorX = px;
                anteriorY = py;
            }
        } else {
            grafico.setColor(new Color(214, 39, 40));
            for (int i = 0; i < cantidad; i++) {
                int px = coordenadaX(i, cantidad, izquierda, anchoPlot);
                int py = coordenadaY(valores[i], arriba, altoPlot);
                grafico.fillRect(px, py, 1, 1);
            }
        }
    }

    private int coordenadaX(int indice, int cantidad, int izquierda, int anchoPlot) {
        if (cantidad <= 1) {
            return izquierda;
        }
        return izquierda + (int) Math.round(((double) indice / (cantidad - 1)) * anchoPlot);
    }

    private int coordenadaY(double valor, int arriba, int altoPlot) {
        double limitado = Math.max(0.0, Math.min(1.0, valor));
        return arriba + (int) Math.round((1.0 - limitado) * altoPlot);
    }

    private static String linea(char caracter, int cantidad) {
        StringBuilder texto = new StringBuilder(cantidad);
        for (int i = 0; i < cantidad; i++) {
            texto.append(caracter);
        }
        return texto.toString();
    }

    public static class FilaComparacion {
        public final int n;
        public final long xPython;
        public final long xJava;
        public final double uPython;
        public final double uJava;
        public final boolean igual;

        public FilaComparacion(
            int n,
            long xPython,
            long xJava,
            double uPython,
            double uJava,
            boolean igual
        ) {
            this.n = n;
            this.xPython = xPython;
            this.xJava = xJava;
            this.uPython = uPython;
            this.uJava = uJava;
            this.igual = igual;
        }
    }
}
