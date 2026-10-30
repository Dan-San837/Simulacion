import java.io.IOException;

public class main {
    public static void main(String[] args) {
        try {
            new controller().ejecutar();
        } catch (IOException excepcion) {
            System.err.println("Error de entrada/salida: " + excepcion.getMessage());
            excepcion.printStackTrace();
        }
    }
}
