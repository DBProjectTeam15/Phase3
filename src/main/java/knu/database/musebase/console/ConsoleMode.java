package knu.database.musebase.console;

public enum ConsoleMode {
    MANAGER,
    MAIN;

    public String toLowerCase() {
        return this.name().toLowerCase();
    }
}
