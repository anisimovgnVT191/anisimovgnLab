package tech.reliab.course.anisimov.exception;

public class UnuniqeIdException extends Exception {
    public UnuniqeIdException(String id) {
        super("Система уже содержит объект с id = " + id);
    }
}
