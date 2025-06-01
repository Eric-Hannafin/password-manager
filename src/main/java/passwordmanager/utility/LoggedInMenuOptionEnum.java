package passwordmanager.utility;

public enum LoggedInMenuOptionEnum {

    ADD("1"),
    RETRIEVE("2"),
    LIST("3"),
    DELETE("4"),
    EXIT("5");

    private final String code;

    LoggedInMenuOptionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LoggedInMenuOptionEnum fromInput(String input){
        for(LoggedInMenuOptionEnum option : values()){
            if(option.code.equals(input)){
                return option;
            }
        }
        return null;
    }
}