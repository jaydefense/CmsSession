package com.application.cms.service;

import com.application.cms.service.dto.MenuTypeDTO;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing MenuType.
 */
public interface MenuTypeService {

    /**
     * Save a menuType.
     *
     * @param menuTypeDTO the entity to save
     * @return the persisted entity
     */
    MenuTypeDTO save(MenuTypeDTO menuTypeDTO);

    /**
     *  Get all the menuTypes.
     *  
     *  @return the list of entities
     */
    List<MenuTypeDTO> findAll();

    /**
     *  Get the "id" menuType.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    MenuTypeDTO findOne(Long id);

    /**
     *  Delete the "id" menuType.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the menuType corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @return the list of entities
     */
    List<MenuTypeDTO> search(String query);
}
