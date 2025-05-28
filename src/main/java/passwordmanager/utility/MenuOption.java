package passwordmanager.utility;

public enum MenuOption {
    LOGIN("1"),
    REGISTER("2"),
    EXIT("3");

    private final String code;

    MenuOption(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MenuOption fromInput(String input) {
        for (MenuOption option : values()) {
            if (option.code.equals(input)) {
                return option;
            }
        }
        return null;
    }
}