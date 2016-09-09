package com.application.cms.service.impl;

import com.application.cms.service.MenuTypeService;
import com.application.cms.domain.MenuType;
import com.application.cms.repository.MenuTypeRepository;
import com.application.cms.repository.search.MenuTypeSearchRepository;
import com.application.cms.service.dto.MenuTypeDTO;
import com.application.cms.service.mapper.MenuTypeMapper;
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
 * Service Implementation for managing MenuType.
 */
@Service
@Transactional
public class MenuTypeServiceImpl implements MenuTypeService{

    private final Logger log = LoggerFactory.getLogger(MenuTypeServiceImpl.class);
    
    @Inject
    private MenuTypeRepository menuTypeRepository;

    @Inject
    private MenuTypeMapper menuTypeMapper;

    @Inject
    private MenuTypeSearchRepository menuTypeSearchRepository;

    /**
     * Save a menuType.
     *
     * @param menuTypeDTO the entity to save
     * @return the persisted entity
     */
    public MenuTypeDTO save(MenuTypeDTO menuTypeDTO) {
        log.debug("Request to save MenuType : {}", menuTypeDTO);
        MenuType menuType = menuTypeMapper.menuTypeDTOToMenuType(menuTypeDTO);
        menuType = menuTypeRepository.save(menuType);
        MenuTypeDTO result = menuTypeMapper.menuTypeToMenuTypeDTO(menuType);
        menuTypeSearchRepository.save(menuType);
        return result;
    }

    /**
     *  Get all the menuTypes.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<MenuTypeDTO> findAll() {
        log.debug("Request to get all MenuTypes");
        List<MenuTypeDTO> result = menuTypeRepository.findAll().stream()
            .map(menuTypeMapper::menuTypeToMenuTypeDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one menuType by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public MenuTypeDTO findOne(Long id) {
        log.debug("Request to get MenuType : {}", id);
        MenuType menuType = menuTypeRepository.findOne(id);
        MenuTypeDTO menuTypeDTO = menuTypeMapper.menuTypeToMenuTypeDTO(menuType);
        return menuTypeDTO;
    }

    /**
     *  Delete the  menuType by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MenuType : {}", id);
        menuTypeRepository.delete(id);
        menuTypeSearchRepository.delete(id);
    }

    /**
     * Search for the menuType corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<MenuTypeDTO> search(String query) {
        log.debug("Request to search MenuTypes for query {}", query);
        return StreamSupport
            .stream(menuTypeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(menuTypeMapper::menuTypeToMenuTypeDTO)
            .collect(Collectors.toList());
    }
}
