package Exceptions;

public class MessageLengthIncorrectException extends RuntimeException {
    public MessageLengthIncorrectException(String message) {
        super(message);
    }
}
