package Enum;

public enum AnsiCodes {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    MAGENTA("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    DEFAULT("\u001B[39m"),
    BACKGROUND_BLACK("\u001B[40m"),
    BACKGROUND_WHITE("\u001B[47m");

    private final String code;

    AnsiCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}