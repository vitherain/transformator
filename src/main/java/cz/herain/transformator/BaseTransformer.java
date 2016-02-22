package cz.herain.transformator;

import java.util.List;

public interface BaseTransformer<E, DTO> {

    DTO transformToDTO(E entity);

    List<DTO> transformToDTOList(List<E> entities);

    E transformToEntity(DTO dto);

    List<E> transformToEntityList(List<DTO> dtos);
}
