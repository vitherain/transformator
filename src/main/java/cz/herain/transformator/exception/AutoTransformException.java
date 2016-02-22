package cz.herain.transformator.exception;

public class AutoTransformException extends RuntimeException {

    public AutoTransformException() {
    }

    public AutoTransformException(String message) {
        super(message);
    }

    public AutoTransformException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutoTransformException(Throwable cause) {
        super(cause);
    }
}
