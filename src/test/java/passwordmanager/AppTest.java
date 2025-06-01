package passwordmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passwordmanager.authentication.AuthenticationService;
import passwordmanager.database.DatabaseService;
import passwordmanager.security.UserSession;
import passwordmanager.user.UserActionService;
import passwordmanager.utility.ConsoleUtil;

import javax.crypto.SecretKey;

import static org.mockito.Mockito.*;

class AppTest {

    private AuthenticationService mockAuthService;
    private UserActionService mockUserActionService;
    private DatabaseService mockDbService;
    private ConsoleUtil mockConsoleUtil;
    private SecretKey mockSecurityKey;
    private App app;

    @BeforeEach
    void setUp() {
        mockAuthService = mock(AuthenticationService.class);
        mockDbService = mock(DatabaseService.class);
        mockConsoleUtil = mock(ConsoleUtil.class);
        mockSecurityKey = mock(SecretKey.class);
        mockUserActionService = mock(UserActionService.class);
        app = new App(mockAuthService, mockDbService, mockConsoleUtil, mockUserActionService);
    }

    @Test
    void testRun_ExitOptionImmediately() {
        when(mockAuthService.initialDialogue()).thenReturn("3");

        app.run();

        verify(mockDbService, times(1)).init();
        verify(mockAuthService, times(1)).initialDialogue();
        verifyNoMoreInteractions(mockAuthService);
    }

    @Test
    void testRun_LoginFlowOnce() {
        when(mockAuthService.initialDialogue()).thenReturn("1", "3");
        when(mockAuthService.login()).thenReturn(new UserSession("testName", mockSecurityKey));
        when(mockAuthService.registeredUserDialogue()).thenReturn("3", "5");

        app.run();

        verify(mockAuthService, times(2)).initialDialogue();
        verify(mockAuthService, times(1)).login();
        verify(mockConsoleUtil, times(2)).clearConsole();
    }


    @Test
    void testRun_RegisterFlowOnce() {
        when(mockAuthService.initialDialogue()).thenReturn("2", "3");
        doNothing().when(mockAuthService).registerUser();

        app.run();

        verify(mockAuthService, times(2)).initialDialogue();
        verify(mockAuthService).registerUser();
    }

}