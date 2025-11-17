package knu.database.musebase.exception;

public class InvalidLoginStateException extends Exception {
    public InvalidLoginStateException() {
        super("Invalid login state");
    }
}
