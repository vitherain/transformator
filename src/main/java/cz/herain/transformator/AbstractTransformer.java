package cz.herain.transformator;

import cz.herain.transformator.annotation.AutoTransform;
import cz.herain.transformator.exception.TransformerCreationException;
import cz.herain.transformator.factory.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractTransformer<E, DTO> implements BaseTransformer<E, DTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransformer.class);

    private final Class<E> entityClass;
    private final Class<DTO> dtoClass;

    private Map<String, Field> entityFieldsIncludingInheritedMap;
    private Map<String, Field> dtoFieldsIncludingInheritedMap;

    public AbstractTransformer(Class<E> entityClass, Class<DTO> dtoClass) {
        this.entityClass = entityClass;
        this.entityFieldsIncludingInheritedMap = getAllFieldsIncludingInheritedMap(entityClass);

        this.dtoClass = dtoClass;
        this.dtoFieldsIncludingInheritedMap = getAllFieldsIncludingInheritedMap(dtoClass);
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
        for (Field field : dtoFieldsIncludingInheritedMap.values()) {
            if (field.isAnnotationPresent(AutoTransform.class)) {
                Field entityField = entityFieldsIncludingInheritedMap.get(field.getName());
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

    private final Map<String, Field> getAllFieldsIncludingInheritedMap(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<>();

        Class<?> current = clazz;

        do {
            Field[] currentClassFields = current.getDeclaredFields();

            for (Field field : currentClassFields) {
                fields.put(field.getName(), field);
            }

            current = current.getSuperclass() != null ? current.getSuperclass() : null;
        } while (current != null);

        return fields;
    }

    private void handleException(Class clazz, ReflectiveOperationException e) {
        String message = "Error while creating of transformer bean of type " + clazz.getSimpleName() + " | " + e.getMessage();
        LOGGER.error(message, e);
        throw new TransformerCreationException(message, e);
    }
}
