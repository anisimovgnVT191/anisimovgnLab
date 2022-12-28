package tech.reliab.course.anisimov.exception;

public class IssuingCreditException extends Exception {
    public IssuingCreditException() {
        super("Кредит не может быть выдан");
    }
}
