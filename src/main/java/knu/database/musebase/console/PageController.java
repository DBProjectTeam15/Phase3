package knu.database.musebase.console;

import knu.database.musebase.exception.InvalidLoginStateException;

public interface PageController<T extends Enum<T>> {

    public void displayScreen() throws InvalidLoginStateException;

    public T invoke(String[] commands);
}
