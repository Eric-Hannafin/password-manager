package passwordmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passwordmanager.authentication.AuthenticationService;
import passwordmanager.database.DatabaseService;
import passwordmanager.utility.ConsoleUtil;

import static org.mockito.Mockito.*;

class AppTest {

    private AuthenticationService mockAuthService;
    private DatabaseService mockDbService;
    private ConsoleUtil mockConsoleUtil;
    private App app;

    @BeforeEach
    void setUp() {
        mockAuthService = mock(AuthenticationService.class);
        mockDbService = mock(DatabaseService.class);
        mockConsoleUtil = mock(ConsoleUtil.class);
        app = new App(mockAuthService, mockDbService, mockConsoleUtil);
    }

    @Test
    void testRun_ExitOptionImmediately() {
        // Simulate user input selecting EXIT immediately
        when(mockAuthService.initialDialogue()).thenReturn("3");

        app.run();

        verify(mockDbService, times(1)).init();
        verify(mockAuthService, times(1)).initialDialogue();
        verifyNoMoreInteractions(mockAuthService);
    }

    @Test
    void testRun_LoginFlowOnce() {
        when(mockAuthService.initialDialogue()).thenReturn("1", "3"); // Login, then Exit
        doNothing().when(mockAuthService).login();

        app.run();

        verify(mockAuthService, times(2)).initialDialogue();
        verify(mockAuthService, times(1)).login();
        verify(mockConsoleUtil, times(1)).clearConsole();
    }

    @Test
    void testRun_RegisterFlowOnce() {
        when(mockAuthService.initialDialogue()).thenReturn("2", "3"); // Register, then Exit
        doNothing().when(mockAuthService).registerUser();

        app.run();

        verify(mockAuthService, times(2)).initialDialogue();
        verify(mockAuthService, times(1)).registerUser();
    }
}