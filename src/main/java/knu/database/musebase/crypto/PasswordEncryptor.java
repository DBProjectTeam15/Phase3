package knu.database.musebase.crypto;

public interface PasswordEncryptor {
    public String getPasswordHash(String password);
}
