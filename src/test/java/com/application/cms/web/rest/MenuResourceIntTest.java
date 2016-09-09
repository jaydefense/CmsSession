package com.application.cms.web.rest;

import com.application.cms.CmsApp;
import com.application.cms.domain.Menu;
import com.application.cms.repository.MenuRepository;
import com.application.cms.service.MenuService;
import com.application.cms.repository.search.MenuSearchRepository;
import com.application.cms.service.dto.MenuDTO;
import com.application.cms.service.mapper.MenuMapper;

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
 * Test class for the MenuResource REST controller.
 *
 * @see MenuResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CmsApp.class)
public class MenuResourceIntTest {

    private static final Integer DEFAULT_ACCESS = 1;
    private static final Integer UPDATED_ACCESS = 2;
    private static final String DEFAULT_ALIAS = "AAAAA";
    private static final String UPDATED_ALIAS = "BBBBB";

    private static final Boolean DEFAULT_HOME = false;
    private static final Boolean UPDATED_HOME = true;
    private static final String DEFAULT_IMG = "AAAAA";
    private static final String UPDATED_IMG = "BBBBB";

    private static final Integer DEFAULT_LEVEL = 1;
    private static final Integer UPDATED_LEVEL = 2;
    private static final String DEFAULT_LINK = "AAAAA";
    private static final String UPDATED_LINK = "BBBBB";
    private static final String DEFAULT_PARAMS = "AAAAA";
    private static final String UPDATED_PARAMS = "BBBBB";
    private static final String DEFAULT_PATH = "AAAAA";
    private static final String UPDATED_PATH = "BBBBB";

    private static final Boolean DEFAULT_PUBLISHED = false;
    private static final Boolean UPDATED_PUBLISHED = true;
    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private MenuRepository menuRepository;

    @Inject
    private MenuMapper menuMapper;

    @Inject
    private MenuService menuService;

    @Inject
    private MenuSearchRepository menuSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restMenuMockMvc;

    private Menu menu;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MenuResource menuResource = new MenuResource();
        ReflectionTestUtils.setField(menuResource, "menuService", menuService);
        this.restMenuMockMvc = MockMvcBuilders.standaloneSetup(menuResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Menu createEntity(EntityManager em) {
        Menu menu = new Menu();
        menu = new Menu()
                .access(DEFAULT_ACCESS)
                .alias(DEFAULT_ALIAS)
                .home(DEFAULT_HOME)
                .img(DEFAULT_IMG)
                .level(DEFAULT_LEVEL)
                .link(DEFAULT_LINK)
                .params(DEFAULT_PARAMS)
                .path(DEFAULT_PATH)
                .published(DEFAULT_PUBLISHED)
                .title(DEFAULT_TITLE)
                .description(DEFAULT_DESCRIPTION);
        return menu;
    }

    @Before
    public void initTest() {
        menuSearchRepository.deleteAll();
        menu = createEntity(em);
    }

    @Test
    @Transactional
    public void createMenu() throws Exception {
        int databaseSizeBeforeCreate = menuRepository.findAll().size();

        // Create the Menu
        MenuDTO menuDTO = menuMapper.menuToMenuDTO(menu);

        restMenuMockMvc.perform(post("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menuDTO)))
                .andExpect(status().isCreated());

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeCreate + 1);
        Menu testMenu = menus.get(menus.size() - 1);
        assertThat(testMenu.getAccess()).isEqualTo(DEFAULT_ACCESS);
        assertThat(testMenu.getAlias()).isEqualTo(DEFAULT_ALIAS);
        assertThat(testMenu.isHome()).isEqualTo(DEFAULT_HOME);
        assertThat(testMenu.getImg()).isEqualTo(DEFAULT_IMG);
        assertThat(testMenu.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testMenu.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testMenu.getParams()).isEqualTo(DEFAULT_PARAMS);
        assertThat(testMenu.getPath()).isEqualTo(DEFAULT_PATH);
        assertThat(testMenu.isPublished()).isEqualTo(DEFAULT_PUBLISHED);
        assertThat(testMenu.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testMenu.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Menu in ElasticSearch
        Menu menuEs = menuSearchRepository.findOne(testMenu.getId());
        assertThat(menuEs).isEqualToComparingFieldByField(testMenu);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = menuRepository.findAll().size();
        // set the field null
        menu.setTitle(null);

        // Create the Menu, which fails.
        MenuDTO menuDTO = menuMapper.menuToMenuDTO(menu);

        restMenuMockMvc.perform(post("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menuDTO)))
                .andExpect(status().isBadRequest());

        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMenus() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);

        // Get all the menus
        restMenuMockMvc.perform(get("/api/menus?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(menu.getId().intValue())))
                .andExpect(jsonPath("$.[*].access").value(hasItem(DEFAULT_ACCESS)))
                .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS.toString())))
                .andExpect(jsonPath("$.[*].home").value(hasItem(DEFAULT_HOME.booleanValue())))
                .andExpect(jsonPath("$.[*].img").value(hasItem(DEFAULT_IMG.toString())))
                .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
                .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
                .andExpect(jsonPath("$.[*].params").value(hasItem(DEFAULT_PARAMS.toString())))
                .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())))
                .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED.booleanValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);

        // Get the menu
        restMenuMockMvc.perform(get("/api/menus/{id}", menu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(menu.getId().intValue()))
            .andExpect(jsonPath("$.access").value(DEFAULT_ACCESS))
            .andExpect(jsonPath("$.alias").value(DEFAULT_ALIAS.toString()))
            .andExpect(jsonPath("$.home").value(DEFAULT_HOME.booleanValue()))
            .andExpect(jsonPath("$.img").value(DEFAULT_IMG.toString()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK.toString()))
            .andExpect(jsonPath("$.params").value(DEFAULT_PARAMS.toString()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH.toString()))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED.booleanValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMenu() throws Exception {
        // Get the menu
        restMenuMockMvc.perform(get("/api/menus/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);
        menuSearchRepository.save(menu);
        int databaseSizeBeforeUpdate = menuRepository.findAll().size();

        // Update the menu
        Menu updatedMenu = menuRepository.findOne(menu.getId());
        updatedMenu
                .access(UPDATED_ACCESS)
                .alias(UPDATED_ALIAS)
                .home(UPDATED_HOME)
                .img(UPDATED_IMG)
                .level(UPDATED_LEVEL)
                .link(UPDATED_LINK)
                .params(UPDATED_PARAMS)
                .path(UPDATED_PATH)
                .published(UPDATED_PUBLISHED)
                .title(UPDATED_TITLE)
                .description(UPDATED_DESCRIPTION);
        MenuDTO menuDTO = menuMapper.menuToMenuDTO(updatedMenu);

        restMenuMockMvc.perform(put("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menuDTO)))
                .andExpect(status().isOk());

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeUpdate);
        Menu testMenu = menus.get(menus.size() - 1);
        assertThat(testMenu.getAccess()).isEqualTo(UPDATED_ACCESS);
        assertThat(testMenu.getAlias()).isEqualTo(UPDATED_ALIAS);
        assertThat(testMenu.isHome()).isEqualTo(UPDATED_HOME);
        assertThat(testMenu.getImg()).isEqualTo(UPDATED_IMG);
        assertThat(testMenu.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testMenu.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testMenu.getParams()).isEqualTo(UPDATED_PARAMS);
        assertThat(testMenu.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testMenu.isPublished()).isEqualTo(UPDATED_PUBLISHED);
        assertThat(testMenu.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testMenu.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Menu in ElasticSearch
        Menu menuEs = menuSearchRepository.findOne(testMenu.getId());
        assertThat(menuEs).isEqualToComparingFieldByField(testMenu);
    }

    @Test
    @Transactional
    public void deleteMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);
        menuSearchRepository.save(menu);
        int databaseSizeBeforeDelete = menuRepository.findAll().size();

        // Get the menu
        restMenuMockMvc.perform(delete("/api/menus/{id}", menu.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean menuExistsInEs = menuSearchRepository.exists(menu.getId());
        assertThat(menuExistsInEs).isFalse();

        // Validate the database is empty
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);
        menuSearchRepository.save(menu);

        // Search the menu
        restMenuMockMvc.perform(get("/api/_search/menus?query=id:" + menu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menu.getId().intValue())))
            .andExpect(jsonPath("$.[*].access").value(hasItem(DEFAULT_ACCESS)))
            .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS.toString())))
            .andExpect(jsonPath("$.[*].home").value(hasItem(DEFAULT_HOME.booleanValue())))
            .andExpect(jsonPath("$.[*].img").value(hasItem(DEFAULT_IMG.toString())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
            .andExpect(jsonPath("$.[*].params").value(hasItem(DEFAULT_PARAMS.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED.booleanValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
