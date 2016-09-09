package com.application.cms.service.mapper;

import com.application.cms.domain.*;
import com.application.cms.service.dto.LanguageDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Language and its DTO LanguageDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LanguageMapper {

    LanguageDTO languageToLanguageDTO(Language language);

    List<LanguageDTO> languagesToLanguageDTOs(List<Language> languages);

    Language languageDTOToLanguage(LanguageDTO languageDTO);

    List<Language> languageDTOsToLanguages(List<LanguageDTO> languageDTOs);
}
