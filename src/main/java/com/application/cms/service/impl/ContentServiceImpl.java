package com.application.cms.service.impl;

import com.application.cms.service.ContentService;
import com.application.cms.domain.Content;
import com.application.cms.repository.ContentRepository;
import com.application.cms.repository.search.ContentSearchRepository;
import com.application.cms.service.dto.ContentDTO;
import com.application.cms.service.mapper.ContentMapper;
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
 * Service Implementation for managing Content.
 */
@Service
@Transactional
public class ContentServiceImpl implements ContentService{

    private final Logger log = LoggerFactory.getLogger(ContentServiceImpl.class);
    
    @Inject
    private ContentRepository contentRepository;

    @Inject
    private ContentMapper contentMapper;

    @Inject
    private ContentSearchRepository contentSearchRepository;

    /**
     * Save a content.
     *
     * @param contentDTO the entity to save
     * @return the persisted entity
     */
    public ContentDTO save(ContentDTO contentDTO) {
        log.debug("Request to save Content : {}", contentDTO);
        Content content = contentMapper.contentDTOToContent(contentDTO);
        content = contentRepository.save(content);
        ContentDTO result = contentMapper.contentToContentDTO(content);
        contentSearchRepository.save(content);
        return result;
    }

    /**
     *  Get all the contents.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<ContentDTO> findAll() {
        log.debug("Request to get all Contents");
        List<ContentDTO> result = contentRepository.findAll().stream()
            .map(contentMapper::contentToContentDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one content by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContentDTO findOne(Long id) {
        log.debug("Request to get Content : {}", id);
        Content content = contentRepository.findOne(id);
        ContentDTO contentDTO = contentMapper.contentToContentDTO(content);
        return contentDTO;
    }

    /**
     *  Delete the  content by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Content : {}", id);
        contentRepository.delete(id);
        contentSearchRepository.delete(id);
    }

    /**
     * Search for the content corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ContentDTO> search(String query) {
        log.debug("Request to search Contents for query {}", query);
        return StreamSupport
            .stream(contentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(contentMapper::contentToContentDTO)
            .collect(Collectors.toList());
    }
}
