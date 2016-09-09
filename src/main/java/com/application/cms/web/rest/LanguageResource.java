package com.application.cms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.application.cms.service.LanguageService;
import com.application.cms.web.rest.util.HeaderUtil;
import com.application.cms.service.dto.LanguageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Language.
 */
@RestController
@RequestMapping("/api")
public class LanguageResource {

    private final Logger log = LoggerFactory.getLogger(LanguageResource.class);
        
    @Inject
    private LanguageService languageService;

    /**
     * POST  /languages : Create a new language.
     *
     * @param languageDTO the languageDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new languageDTO, or with status 400 (Bad Request) if the language has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/languages",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LanguageDTO> createLanguage(@Valid @RequestBody LanguageDTO languageDTO) throws URISyntaxException {
        log.debug("REST request to save Language : {}", languageDTO);
        if (languageDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("language", "idexists", "A new language cannot already have an ID")).body(null);
        }
        LanguageDTO result = languageService.save(languageDTO);
        return ResponseEntity.created(new URI("/api/languages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("language", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /languages : Updates an existing language.
     *
     * @param languageDTO the languageDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated languageDTO,
     * or with status 400 (Bad Request) if the languageDTO is not valid,
     * or with status 500 (Internal Server Error) if the languageDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/languages",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LanguageDTO> updateLanguage(@Valid @RequestBody LanguageDTO languageDTO) throws URISyntaxException {
        log.debug("REST request to update Language : {}", languageDTO);
        if (languageDTO.getId() == null) {
            return createLanguage(languageDTO);
        }
        LanguageDTO result = languageService.save(languageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("language", languageDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /languages : get all the languages.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of languages in body
     */
    @RequestMapping(value = "/languages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<LanguageDTO> getAllLanguages() {
        log.debug("REST request to get all Languages");
        return languageService.findAll();
    }

    /**
     * GET  /languages/:id : get the "id" language.
     *
     * @param id the id of the languageDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the languageDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/languages/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LanguageDTO> getLanguage(@PathVariable Long id) {
        log.debug("REST request to get Language : {}", id);
        LanguageDTO languageDTO = languageService.findOne(id);
        return Optional.ofNullable(languageDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /languages/:id : delete the "id" language.
     *
     * @param id the id of the languageDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/languages/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) {
        log.debug("REST request to delete Language : {}", id);
        languageService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("language", id.toString())).build();
    }

    /**
     * SEARCH  /_search/languages?query=:query : search for the language corresponding
     * to the query.
     *
     * @param query the query of the language search 
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/languages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<LanguageDTO> searchLanguages(@RequestParam String query) {
        log.debug("REST request to search Languages for query {}", query);
        return languageService.search(query);
    }


}
