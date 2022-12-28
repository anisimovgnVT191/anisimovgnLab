package tech.reliab.course.anisimov.exception;

public class DoesNotExistException extends Exception {
    public DoesNotExistException(String id) {
        super("В системе не существует объекта с id = " + id);
    }
}
