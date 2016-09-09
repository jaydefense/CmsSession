package com.application.cms.service.impl;

import com.application.cms.service.RoleService;
import com.application.cms.domain.Role;
import com.application.cms.repository.RoleRepository;
import com.application.cms.repository.search.RoleSearchRepository;
import com.application.cms.service.dto.RoleDTO;
import com.application.cms.service.mapper.RoleMapper;
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
 * Service Implementation for managing Role.
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService{

    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    
    @Inject
    private RoleRepository roleRepository;

    @Inject
    private RoleMapper roleMapper;

    @Inject
    private RoleSearchRepository roleSearchRepository;

    /**
     * Save a role.
     *
     * @param roleDTO the entity to save
     * @return the persisted entity
     */
    public RoleDTO save(RoleDTO roleDTO) {
        log.debug("Request to save Role : {}", roleDTO);
        Role role = roleMapper.roleDTOToRole(roleDTO);
        role = roleRepository.save(role);
        RoleDTO result = roleMapper.roleToRoleDTO(role);
        roleSearchRepository.save(role);
        return result;
    }

    /**
     *  Get all the roles.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<RoleDTO> findAll() {
        log.debug("Request to get all Roles");
        List<RoleDTO> result = roleRepository.findAll().stream()
            .map(roleMapper::roleToRoleDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one role by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public RoleDTO findOne(Long id) {
        log.debug("Request to get Role : {}", id);
        Role role = roleRepository.findOne(id);
        RoleDTO roleDTO = roleMapper.roleToRoleDTO(role);
        return roleDTO;
    }

    /**
     *  Delete the  role by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Role : {}", id);
        roleRepository.delete(id);
        roleSearchRepository.delete(id);
    }

    /**
     * Search for the role corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> search(String query) {
        log.debug("Request to search Roles for query {}", query);
        return StreamSupport
            .stream(roleSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(roleMapper::roleToRoleDTO)
            .collect(Collectors.toList());
    }
}
