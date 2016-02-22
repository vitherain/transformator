package cz.herain.transformator.exception;

public class TransformerCreationException extends RuntimeException {

    public TransformerCreationException() {
    }

    public TransformerCreationException(String message) {
        super(message);
    }

    public TransformerCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformerCreationException(Throwable cause) {
        super(cause);
    }
}
