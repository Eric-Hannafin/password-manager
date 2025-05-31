package passwordmanager.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleUtil.class);

    private final String osName;
    private final String termEnv;

    public ConsoleUtil(String osName, String termEnv) {
        this.osName = osName.toLowerCase();
        this.termEnv = termEnv;
    }

    public ConsoleUtil() {
        this("Windows", "xterm");
    }


    public void clearConsole() {
        LOGGER.info("Clearing console");

        try {
            if (termEnv != null && termEnv.toLowerCase().contains("xterm")) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                return;
            }

            if (osName.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred while trying to clear the console", e);
        }
    }
}