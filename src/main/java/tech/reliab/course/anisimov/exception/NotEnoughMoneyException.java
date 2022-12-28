package tech.reliab.course.anisimov.exception;

public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException() {
        super("Недостаточно средств");
    }
}
