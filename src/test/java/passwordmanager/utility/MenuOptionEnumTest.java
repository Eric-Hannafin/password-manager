package passwordmanager.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MenuOptionEnumTest {

    @Test
    void returnsCorrectEnumForValidInput() {
        Assertions.assertEquals(MenuOptionEnum.LOGIN, MenuOptionEnum.fromInput("1"));
        Assertions.assertEquals(MenuOptionEnum.REGISTER, MenuOptionEnum.fromInput("2"));
        Assertions.assertEquals(MenuOptionEnum.EXIT, MenuOptionEnum.fromInput("3"));
    }

    @Test
    void returnsNullForInvalidInput() {
        Assertions.assertNull(MenuOptionEnum.fromInput("4"));
        Assertions.assertNull(MenuOptionEnum.fromInput("invalid"));
        Assertions.assertNull(MenuOptionEnum.fromInput(""));
    }

    @Test
    void handlesNullInputGracefully() {
        Assertions.assertNull(MenuOptionEnum.fromInput(null));
    }
}