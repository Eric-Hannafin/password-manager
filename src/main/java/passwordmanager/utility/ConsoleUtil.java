package passwordmanager.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleUtil.class);

    private ConsoleUtil() {}

    public static void clearConsole() {
        LOGGER.info("Clearing console");
        String os = System.getProperty("os.name").toLowerCase();

        try {
            String term = System.getenv("TERM");
            if (term != null && term.toLowerCase().contains("xterm")) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                return;
            }

            if (os.contains("windows")) {
                try {
                    new ProcessBuilder("C:\\Windows\\System32\\cmd.exe", "/c", "cls")
                            .inheritIO()
                            .start()
                            .waitFor();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            } else {
                try {
                    new ProcessBuilder("/usr/bin/clear")
                            .inheritIO()
                            .start()
                            .waitFor();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred while trying to clear the console", e);
        }
    }
}