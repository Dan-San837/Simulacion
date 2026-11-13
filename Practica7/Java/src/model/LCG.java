package model;

public class LCG {
    private long state;

    public LCG(long seed) {
        this.state = seed & 0x7fffffffL;
    }

    public int nextInt() {
        state = (1103515245L * state + 12345L) & 0x7fffffffL;
        return (int) state;
    }

    public double random() {
        return ((nextInt() & 0x7fffffffL) / 2147483648.0);
    }
}
