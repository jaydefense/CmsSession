package com.application.cms.service.impl;

import com.application.cms.service.LanguageService;
import com.application.cms.domain.Language;
import com.application.cms.repository.LanguageRepository;
import com.application.cms.repository.search.LanguageSearchRepository;
import com.application.cms.service.dto.LanguageDTO;
import com.application.cms.service.mapper.LanguageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Language.
 */
@Service
@Transactional
public class LanguageServiceImpl implements LanguageService{

    private final Logger log = LoggerFactory.getLogger(LanguageServiceImpl.class);
    
    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private LanguageMapper languageMapper;

    @Inject
    private LanguageSearchRepository languageSearchRepository;

    /**
     * Save a language.
     *
     * @param languageDTO the entity to save
     * @return the persisted entity
     */
    public LanguageDTO save(LanguageDTO languageDTO) {
        log.debug("Request to save Language : {}", languageDTO);
        Language language = languageMapper.languageDTOToLanguage(languageDTO);
        language = languageRepository.save(language);
        LanguageDTO result = languageMapper.languageToLanguageDTO(language);
        languageSearchRepository.save(language);
        return result;
    }

    /**
     *  Get all the languages.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<LanguageDTO> findAll() {
        log.debug("Request to get all Languages");
        List<LanguageDTO> result = languageRepository.findAll().stream()
            .map(languageMapper::languageToLanguageDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one language by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public LanguageDTO findOne(Long id) {
        log.debug("Request to get Language : {}", id);
        Language language = languageRepository.findOne(id);
        LanguageDTO languageDTO = languageMapper.languageToLanguageDTO(language);
        return languageDTO;
    }

    /**
     *  Delete the  language by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Language : {}", id);
        languageRepository.delete(id);
        languageSearchRepository.delete(id);
    }

    /**
     * Search for the language corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<LanguageDTO> search(String query) {
        log.debug("Request to search Languages for query {}", query);
        return StreamSupport
            .stream(languageSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(languageMapper::languageToLanguageDTO)
            .collect(Collectors.toList());
    }
}
