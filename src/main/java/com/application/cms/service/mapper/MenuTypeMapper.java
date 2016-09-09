package com.application.cms.service.mapper;

import com.application.cms.domain.*;
import com.application.cms.service.dto.MenuTypeDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity MenuType and its DTO MenuTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MenuTypeMapper {

    MenuTypeDTO menuTypeToMenuTypeDTO(MenuType menuType);

    List<MenuTypeDTO> menuTypesToMenuTypeDTOs(List<MenuType> menuTypes);

    MenuType menuTypeDTOToMenuType(MenuTypeDTO menuTypeDTO);

    List<MenuType> menuTypeDTOsToMenuTypes(List<MenuTypeDTO> menuTypeDTOs);
}
