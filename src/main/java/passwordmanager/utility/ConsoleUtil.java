package passwordmanager.utility;

public class ConsoleUtil {

    private ConsoleUtil(){}

    public static void clearConsole(){
        System.out.println("Clearing console using ProcessBuilder");
        String os = System.getProperty("os.name");
        try {
            if (os.startsWith("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Error while clearing console: " + e.getMessage());

        }
    }
}