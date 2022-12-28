package tech.reliab.course.anisimov.exception;

public class CannotDepositMoneyException extends Exception{
    public CannotDepositMoneyException() {
        super("Внесение наличных невозможно");
    }
}
