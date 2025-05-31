package passwordmanager.utility;

public enum MenuOptionEnum {
    LOGIN("1"),
    REGISTER("2"),
    EXIT("3");

    private final String code;

    MenuOptionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MenuOptionEnum fromInput(String input) {
        for (MenuOptionEnum option : values()) {
            if (option.code.equals(input)) {
                return option;
            }
        }
        return null;
    }
}