import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class model {
    private final long a;
    private final long c;
    private final long m;
    private final long semilla;
    private long estado;
    private long generados;

    public model() {
        this(1664525L, 1013904223L, 1L << 32, 12345L);
    }

    public model(long a, long c, long m, long semilla) {
        if (m <= 0) {
            throw new IllegalArgumentException("El modulo m debe ser positivo.");
        }
        this.a = a;
        this.c = c;
        this.m = m;
        this.semilla = semilla % m;
        this.estado = this.semilla;
        this.generados = 0;
    }

    public void reiniciar() {
        this.estado = this.semilla;
        this.generados = 0;
    }

    public long siguiente() {
        this.estado = (this.a * this.estado + this.c) % this.m;
        this.generados++;
        return this.estado;
    }

    public double siguienteNormalizado() {
        return (double) siguiente() / (double) this.m;
    }

    public long[] generar(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad debe ser no negativa.");
        }
        long[] valores = new long[cantidad];
        for (int i = 0; i < cantidad; i++) {
            valores[i] = siguiente();
        }
        return valores;
    }

    public double[] normalizar(long[] estados) {
        double[] valores = new double[estados.length];
        for (int i = 0; i < estados.length; i++) {
            valores[i] = (double) estados[i] / (double) this.m;
        }
        return valores;
    }

    public double normalizar(long estado) {
        return (double) estado / (double) this.m;
    }

    public long a() {
        return this.a;
    }

    public long c() {
        return this.c;
    }

    public long m() {
        return this.m;
    }

    public long semilla() {
        return this.semilla;
    }

    public long generados() {
        return this.generados;
    }

    public Periodo analizarPeriodo(long[] muestra) {
        long[] factores = factoresPrimos(this.m);
        boolean mcdCumple = mcd(this.c, this.m) == 1;
        boolean factoresCumplen = true;
        for (long factor : factores) {
            if ((this.a - 1) % factor != 0) {
                factoresCumplen = false;
                break;
            }
        }
        boolean condicionCuatroCumple = this.m % 4 != 0 || (this.a - 1) % 4 == 0;
        boolean periodoCompleto = mcdCumple && factoresCumplen && condicionCuatroCumple;

        Set<Long> vistos = new HashSet<>();
        boolean repetidos = false;
        for (long valor : muestra) {
            if (!vistos.add(valor)) {
                repetidos = true;
                break;
            }
        }

        return new Periodo(
            factores,
            mcdCumple,
            factoresCumplen,
            condicionCuatroCumple,
            periodoCompleto,
            periodoCompleto ? this.m : 0,
            muestra.length,
            repetidos
        );
    }

    private static long mcd(long x, long y) {
        long a = x;
        long b = y;
        while (b != 0) {
            long temporal = b;
            b = a % b;
            a = temporal;
        }
        return a;
    }

    private static long[] factoresPrimos(long numero) {
        List<Long> factores = new ArrayList<>();
        long n = numero;
        for (long divisor = 2; divisor * divisor <= n; divisor++) {
            if (n % divisor == 0) {
                factores.add(divisor);
                while (n % divisor == 0) {
                    n /= divisor;
                }
            }
        }
        if (n > 1) {
            factores.add(n);
        }

        long[] resultado = new long[factores.size()];
        for (int i = 0; i < factores.size(); i++) {
            resultado[i] = factores.get(i);
        }
        return resultado;
    }

    public static class Periodo {
        public final long[] factoresPrimosM;
        public final boolean mcdCumple;
        public final boolean factoresCumplen;
        public final boolean condicionCuatroCumple;
        public final boolean periodoCompleto;
        public final long periodoTeorico;
        public final int muestra;
        public final boolean repetidosEnMuestra;

        public Periodo(
            long[] factoresPrimosM,
            boolean mcdCumple,
            boolean factoresCumplen,
            boolean condicionCuatroCumple,
            boolean periodoCompleto,
            long periodoTeorico,
            int muestra,
            boolean repetidosEnMuestra
        ) {
            this.factoresPrimosM = factoresPrimosM;
            this.mcdCumple = mcdCumple;
            this.factoresCumplen = factoresCumplen;
            this.condicionCuatroCumple = condicionCuatroCumple;
            this.periodoCompleto = periodoCompleto;
            this.periodoTeorico = periodoTeorico;
            this.muestra = muestra;
            this.repetidosEnMuestra = repetidosEnMuestra;
        }
    }
}
