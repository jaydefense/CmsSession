package com.application.cms.service.impl;

import com.application.cms.service.MenuService;
import com.application.cms.domain.Menu;
import com.application.cms.repository.MenuRepository;
import com.application.cms.repository.search.MenuSearchRepository;
import com.application.cms.service.dto.MenuDTO;
import com.application.cms.service.mapper.MenuMapper;
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
 * Service Implementation for managing Menu.
 */
@Service
@Transactional
public class MenuServiceImpl implements MenuService{

    private final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);
    
    @Inject
    private MenuRepository menuRepository;

    @Inject
    private MenuMapper menuMapper;

    @Inject
    private MenuSearchRepository menuSearchRepository;

    /**
     * Save a menu.
     *
     * @param menuDTO the entity to save
     * @return the persisted entity
     */
    public MenuDTO save(MenuDTO menuDTO) {
        log.debug("Request to save Menu : {}", menuDTO);
        Menu menu = menuMapper.menuDTOToMenu(menuDTO);
        menu = menuRepository.save(menu);
        MenuDTO result = menuMapper.menuToMenuDTO(menu);
        menuSearchRepository.save(menu);
        return result;
    }

    /**
     *  Get all the menus.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<MenuDTO> findAll() {
        log.debug("Request to get all Menus");
        List<MenuDTO> result = menuRepository.findAll().stream()
            .map(menuMapper::menuToMenuDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one menu by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public MenuDTO findOne(Long id) {
        log.debug("Request to get Menu : {}", id);
        Menu menu = menuRepository.findOne(id);
        MenuDTO menuDTO = menuMapper.menuToMenuDTO(menu);
        return menuDTO;
    }

    /**
     *  Delete the  menu by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Menu : {}", id);
        menuRepository.delete(id);
        menuSearchRepository.delete(id);
    }

    /**
     * Search for the menu corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<MenuDTO> search(String query) {
        log.debug("Request to search Menus for query {}", query);
        return StreamSupport
            .stream(menuSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(menuMapper::menuToMenuDTO)
            .collect(Collectors.toList());
    }
}
