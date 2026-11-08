public final class main {
    private main() {
    }

    public static void main(String[] args) {
        try {
            controller.run(args);
        } catch (IllegalArgumentException | java.io.IOException exception) {
            System.err.println("Error: " + exception.getMessage());
            view.showUsage();
            System.exit(2);
        }
    }
}