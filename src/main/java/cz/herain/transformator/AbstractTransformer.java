package cz.herain.transformator;

import cz.herain.transformator.annotation.AutoTransform;
import cz.herain.transformator.exception.TransformerCreationException;
import cz.herain.transformator.factory.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTransformer<E, DTO> implements BaseTransformer<E, DTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransformer.class);

    private final Class<E> entityClass;
    private final Class<DTO> dtoClass;

    public AbstractTransformer(Class<E> entityClass, Class<DTO> dtoClass) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public final List<DTO> transformToDTOList(List<E> entities) {
        if (entities != null) {
            return prepareListOfDTOs(entities);
        }

        return new ArrayList<>();
    }

    private List<DTO> prepareListOfDTOs(List<E> entities) {
        List<DTO> list = new LinkedList<>();

        for (E entity : entities) {
            list.add(transformToDTO(entity));
        }
        return list;
    }

    @Override
    public final List<E> transformToEntityList(List<DTO> dtos) {
        if (dtos != null) {
            return prepareListOfEntities(dtos);
        }

        return new ArrayList<>();
    }

    private List<E> prepareListOfEntities(List<DTO> dtos) {
        List<E> list = new LinkedList<>();

        for (DTO dto : dtos) {
            list.add(transformToEntity(dto));
        }
        return list;
    }

    @Override
    public final DTO transformToDTO(E entity) {
        DTO dto = null;

        if (entity != null) {
            dto = ObjectFactory.createNewInstanceOfClass(dtoClass);

            try {
                dto = autoTransformAttributesToDTO(entity, dto);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                handleException(dtoClass, e);
            }

            dto = setSpecificDTOAttributes(entity, dto);
        }

        return dto;
    }

    /**
     * This method has to be overrided in child class in order to achieve transformation of
     * more complex attributes from entity object to DTO object.
     */
    protected DTO setSpecificDTOAttributes(E entity, DTO dto) {
        return dto;
    }

    private DTO autoTransformAttributesToDTO(E entity, DTO dto) throws NoSuchFieldException, IllegalAccessException {
        Field[] dtoFields = dtoClass.getDeclaredFields();

        for (Field field : dtoFields) {
            if (field.isAnnotationPresent(AutoTransform.class)) {
                Field entityField = entityClass.getDeclaredField(field.getName());
                entityField.setAccessible(true);
                Object entityValue = entityField.get(entity);

                field.setAccessible(true);
                field.set(dto, entityValue);
            }
        }

        return dto;
    }

    @Override
    public final E transformToEntity(DTO dto) {
        E entity = null;

        if (dto != null) {
            entity = ObjectFactory.createNewInstanceOfClass(entityClass);

            entity = setSpecificEntityAttributes(dto, entity);
        }

        return entity;
    }

    /**
     * This method has to be overrided in child class in order to achieve transformation of
     * more complex attributes from DTO object to entity object.
     */
    protected E setSpecificEntityAttributes(DTO dto, E entity) {
        return entity;
    }

    private static void handleException(Class clazz, ReflectiveOperationException e) {
        String message = "Error while creating of transformer bean of type " + clazz.getSimpleName() + " | " + e.getMessage();
        LOGGER.error(message, e);
        throw new TransformerCreationException(message, e);
    }
}
