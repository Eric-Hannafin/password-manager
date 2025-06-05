package passwordmanager.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwordmanager.exception.ClearConsoleException;

import java.io.IOException;
import java.util.Scanner;

@SuppressWarnings("squid:S106")
public class ConsoleUtilImpl implements ConsoleUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleUtilImpl.class);

    @Override
    public void clearConsole() {
        LOGGER.info("Clearing console");
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (isXtermTerminal()) {
                clearXtermConsole();
                return;
            }

            if (os.contains("windows")) {
                clearWindowsConsole();
            } else {
                clearUnixConsole();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ClearConsoleException("Console clearing was interrupted", ie);
        } catch (Exception e) {
            LOGGER.error("An error occurred while trying to clear the console", e);
        }
    }

    private boolean isXtermTerminal() {
        String term = System.getenv("TERM");
        return term != null && term.toLowerCase().contains("xterm");
    }

    private void clearXtermConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void clearWindowsConsole() throws InterruptedException, IOException {
        new ProcessBuilder("C:\\Windows\\System32\\cmd.exe", "/c", "cls")
                .inheritIO()
                .start()
                .waitFor();
    }

    private void clearUnixConsole() throws InterruptedException, IOException {
        new ProcessBuilder("/usr/bin/clear")
                .inheritIO()
                .start()
                .waitFor();
    }

    @Override
    public String readLine() {
        return new Scanner(System.in).nextLine();
    }

    @Override
    public void printLine(String stringToPrint) {
        System.out.println(stringToPrint);
    }
}