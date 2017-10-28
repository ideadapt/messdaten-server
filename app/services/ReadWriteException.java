package services;

/**
 * Wird eingesetzt um beim Abfangen von Exceptions die ReadWriteException
 * mit einer entsprechenden Meldung zu werfen.
 *
 * Created by Nett on 28.12.2016.
 * @author Nett
 */
public class ReadWriteException extends RuntimeException{

    public ReadWriteException(String message) {
        super(message);
    }

    public ReadWriteException(String message, Exception exception) {
        super(message, exception);
    }
}
