package com.application.cms.service;

import com.application.cms.service.dto.MenuDTO;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Menu.
 */
public interface MenuService {

    /**
     * Save a menu.
     *
     * @param menuDTO the entity to save
     * @return the persisted entity
     */
    MenuDTO save(MenuDTO menuDTO);

    /**
     *  Get all the menus.
     *  
     *  @return the list of entities
     */
    List<MenuDTO> findAll();

    /**
     *  Get the "id" menu.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    MenuDTO findOne(Long id);

    /**
     *  Delete the "id" menu.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the menu corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @return the list of entities
     */
    List<MenuDTO> search(String query);
}
