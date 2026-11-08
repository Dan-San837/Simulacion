package models;

public final class LCG {
    public static final long DEFAULT_SEED = 1L;
    public static final long DEFAULT_MULTIPLIER = 1_103_515_245L;
    public static final long DEFAULT_INCREMENT = 12_345L;
    public static final long DEFAULT_MODULUS = 2_147_483_648L;

    private final long multiplier;
    private final long increment;
    private final long modulus;
    private long state;

    public LCG(long seed, long multiplier, long increment, long modulus) {
        if (modulus < 2 || modulus > Integer.MAX_VALUE + 1L) {
            throw new IllegalArgumentException("El módulo debe estar entre 2 y 2^31.");
        }
        if (multiplier < 0 || multiplier >= modulus) {
            throw new IllegalArgumentException("El multiplicador debe estar en [0, módulo).");
        }
        if (increment < 0 || increment >= modulus) {
            throw new IllegalArgumentException("El incremento debe estar en [0, módulo).");
        }
        if (seed < 0 || seed >= modulus) {
            throw new IllegalArgumentException("La semilla debe estar en [0, módulo).");
        }
        this.state = seed;
        this.multiplier = multiplier;
        this.increment = increment;
        this.modulus = modulus;
    }

    public long nextInt() {
        state = (multiplier * state + increment) % modulus;
        return state;
    }

    public double nextDouble() {
        return (double) nextInt() / modulus;
    }

    public double[] generate(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("La cantidad de números debe ser positiva.");
        }
        double[] values = new double[count];
        for (int i = 0; i < count; i++) {
            values[i] = nextDouble();
        }
        return values;
    }
}
