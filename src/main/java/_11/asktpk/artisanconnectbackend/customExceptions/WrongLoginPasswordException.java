package _11.asktpk.artisanconnectbackend.customExceptions;

public class WrongLoginPasswordException extends Exception {
    public WrongLoginPasswordException(String message) {
        super(message);
    }
}
