package ru.gbax.xmlpe.common.exception;

/**
 * Ошибка при импорте
 * Created by GBAX on 28.07.2015.
 */
public class ImportException extends Exception {

    private String message;

    public ImportException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
