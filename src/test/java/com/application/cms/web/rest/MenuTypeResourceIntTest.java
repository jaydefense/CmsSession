package com.application.cms.web.rest;

import com.application.cms.CmsApp;
import com.application.cms.domain.MenuType;
import com.application.cms.repository.MenuTypeRepository;
import com.application.cms.service.MenuTypeService;
import com.application.cms.repository.search.MenuTypeSearchRepository;
import com.application.cms.service.dto.MenuTypeDTO;
import com.application.cms.service.mapper.MenuTypeMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MenuTypeResource REST controller.
 *
 * @see MenuTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CmsApp.class)
public class MenuTypeResourceIntTest {
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";

    @Inject
    private MenuTypeRepository menuTypeRepository;

    @Inject
    private MenuTypeMapper menuTypeMapper;

    @Inject
    private MenuTypeService menuTypeService;

    @Inject
    private MenuTypeSearchRepository menuTypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restMenuTypeMockMvc;

    private MenuType menuType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MenuTypeResource menuTypeResource = new MenuTypeResource();
        ReflectionTestUtils.setField(menuTypeResource, "menuTypeService", menuTypeService);
        this.restMenuTypeMockMvc = MockMvcBuilders.standaloneSetup(menuTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuType createEntity(EntityManager em) {
        MenuType menuType = new MenuType();
        menuType = new MenuType()
                .type(DEFAULT_TYPE);
        return menuType;
    }

    @Before
    public void initTest() {
        menuTypeSearchRepository.deleteAll();
        menuType = createEntity(em);
    }

    @Test
    @Transactional
    public void createMenuType() throws Exception {
        int databaseSizeBeforeCreate = menuTypeRepository.findAll().size();

        // Create the MenuType
        MenuTypeDTO menuTypeDTO = menuTypeMapper.menuTypeToMenuTypeDTO(menuType);

        restMenuTypeMockMvc.perform(post("/api/menu-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menuTypeDTO)))
                .andExpect(status().isCreated());

        // Validate the MenuType in the database
        List<MenuType> menuTypes = menuTypeRepository.findAll();
        assertThat(menuTypes).hasSize(databaseSizeBeforeCreate + 1);
        MenuType testMenuType = menuTypes.get(menuTypes.size() - 1);
        assertThat(testMenuType.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the MenuType in ElasticSearch
        MenuType menuTypeEs = menuTypeSearchRepository.findOne(testMenuType.getId());
        assertThat(menuTypeEs).isEqualToComparingFieldByField(testMenuType);
    }

    @Test
    @Transactional
    public void getAllMenuTypes() throws Exception {
        // Initialize the database
        menuTypeRepository.saveAndFlush(menuType);

        // Get all the menuTypes
        restMenuTypeMockMvc.perform(get("/api/menu-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(menuType.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getMenuType() throws Exception {
        // Initialize the database
        menuTypeRepository.saveAndFlush(menuType);

        // Get the menuType
        restMenuTypeMockMvc.perform(get("/api/menu-types/{id}", menuType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(menuType.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMenuType() throws Exception {
        // Get the menuType
        restMenuTypeMockMvc.perform(get("/api/menu-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMenuType() throws Exception {
        // Initialize the database
        menuTypeRepository.saveAndFlush(menuType);
        menuTypeSearchRepository.save(menuType);
        int databaseSizeBeforeUpdate = menuTypeRepository.findAll().size();

        // Update the menuType
        MenuType updatedMenuType = menuTypeRepository.findOne(menuType.getId());
        updatedMenuType
                .type(UPDATED_TYPE);
        MenuTypeDTO menuTypeDTO = menuTypeMapper.menuTypeToMenuTypeDTO(updatedMenuType);

        restMenuTypeMockMvc.perform(put("/api/menu-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menuTypeDTO)))
                .andExpect(status().isOk());

        // Validate the MenuType in the database
        List<MenuType> menuTypes = menuTypeRepository.findAll();
        assertThat(menuTypes).hasSize(databaseSizeBeforeUpdate);
        MenuType testMenuType = menuTypes.get(menuTypes.size() - 1);
        assertThat(testMenuType.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the MenuType in ElasticSearch
        MenuType menuTypeEs = menuTypeSearchRepository.findOne(testMenuType.getId());
        assertThat(menuTypeEs).isEqualToComparingFieldByField(testMenuType);
    }

    @Test
    @Transactional
    public void deleteMenuType() throws Exception {
        // Initialize the database
        menuTypeRepository.saveAndFlush(menuType);
        menuTypeSearchRepository.save(menuType);
        int databaseSizeBeforeDelete = menuTypeRepository.findAll().size();

        // Get the menuType
        restMenuTypeMockMvc.perform(delete("/api/menu-types/{id}", menuType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean menuTypeExistsInEs = menuTypeSearchRepository.exists(menuType.getId());
        assertThat(menuTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<MenuType> menuTypes = menuTypeRepository.findAll();
        assertThat(menuTypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMenuType() throws Exception {
        // Initialize the database
        menuTypeRepository.saveAndFlush(menuType);
        menuTypeSearchRepository.save(menuType);

        // Search the menuType
        restMenuTypeMockMvc.perform(get("/api/_search/menu-types?query=id:" + menuType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuType.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
