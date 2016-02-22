package cz.herain.transformator.factory;

import cz.herain.transformator.exception.TransformerCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectFactory.class);

    public static <T> T createNewInstanceOfClass(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (ReflectiveOperationException e) {
            handleException(clazz, e);
        }

        return null;
    }

    private static void handleException(Class clazz, ReflectiveOperationException e) {
        String message = "Error while creating of transformer bean of type " + clazz.getSimpleName() + " | " + e.getMessage();
        LOGGER.error(message, e);
        throw new TransformerCreationException(message, e);
    }
}
