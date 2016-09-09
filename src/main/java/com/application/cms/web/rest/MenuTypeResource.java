package com.application.cms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.application.cms.service.MenuTypeService;
import com.application.cms.web.rest.util.HeaderUtil;
import com.application.cms.service.dto.MenuTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing MenuType.
 */
@RestController
@RequestMapping("/api")
public class MenuTypeResource {

    private final Logger log = LoggerFactory.getLogger(MenuTypeResource.class);
        
    @Inject
    private MenuTypeService menuTypeService;

    /**
     * POST  /menu-types : Create a new menuType.
     *
     * @param menuTypeDTO the menuTypeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new menuTypeDTO, or with status 400 (Bad Request) if the menuType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/menu-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MenuTypeDTO> createMenuType(@RequestBody MenuTypeDTO menuTypeDTO) throws URISyntaxException {
        log.debug("REST request to save MenuType : {}", menuTypeDTO);
        if (menuTypeDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("menuType", "idexists", "A new menuType cannot already have an ID")).body(null);
        }
        MenuTypeDTO result = menuTypeService.save(menuTypeDTO);
        return ResponseEntity.created(new URI("/api/menu-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("menuType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /menu-types : Updates an existing menuType.
     *
     * @param menuTypeDTO the menuTypeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated menuTypeDTO,
     * or with status 400 (Bad Request) if the menuTypeDTO is not valid,
     * or with status 500 (Internal Server Error) if the menuTypeDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/menu-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MenuTypeDTO> updateMenuType(@RequestBody MenuTypeDTO menuTypeDTO) throws URISyntaxException {
        log.debug("REST request to update MenuType : {}", menuTypeDTO);
        if (menuTypeDTO.getId() == null) {
            return createMenuType(menuTypeDTO);
        }
        MenuTypeDTO result = menuTypeService.save(menuTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("menuType", menuTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /menu-types : get all the menuTypes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of menuTypes in body
     */
    @RequestMapping(value = "/menu-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<MenuTypeDTO> getAllMenuTypes() {
        log.debug("REST request to get all MenuTypes");
        return menuTypeService.findAll();
    }

    /**
     * GET  /menu-types/:id : get the "id" menuType.
     *
     * @param id the id of the menuTypeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the menuTypeDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/menu-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MenuTypeDTO> getMenuType(@PathVariable Long id) {
        log.debug("REST request to get MenuType : {}", id);
        MenuTypeDTO menuTypeDTO = menuTypeService.findOne(id);
        return Optional.ofNullable(menuTypeDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /menu-types/:id : delete the "id" menuType.
     *
     * @param id the id of the menuTypeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/menu-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMenuType(@PathVariable Long id) {
        log.debug("REST request to delete MenuType : {}", id);
        menuTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("menuType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/menu-types?query=:query : search for the menuType corresponding
     * to the query.
     *
     * @param query the query of the menuType search 
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/menu-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<MenuTypeDTO> searchMenuTypes(@RequestParam String query) {
        log.debug("REST request to search MenuTypes for query {}", query);
        return menuTypeService.search(query);
    }


}
