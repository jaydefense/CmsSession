package com.application.cms.service.mapper;

import com.application.cms.domain.*;
import com.application.cms.service.dto.ContentDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Content and its DTO ContentDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ContentMapper {

    @Mapping(source = "language.id", target = "languageId")
    @Mapping(source = "category.id", target = "categoryId")
    ContentDTO contentToContentDTO(Content content);

    List<ContentDTO> contentsToContentDTOs(List<Content> contents);

    @Mapping(source = "languageId", target = "language")
    @Mapping(source = "categoryId", target = "category")
    Content contentDTOToContent(ContentDTO contentDTO);

    List<Content> contentDTOsToContents(List<ContentDTO> contentDTOs);

    default Language languageFromId(Long id) {
        if (id == null) {
            return null;
        }
        Language language = new Language();
        language.setId(id);
        return language;
    }

    default Category categoryFromId(Long id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
