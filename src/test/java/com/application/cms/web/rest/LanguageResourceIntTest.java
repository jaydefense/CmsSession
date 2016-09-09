package com.application.cms.web.rest;

import com.application.cms.CmsApp;
import com.application.cms.domain.Language;
import com.application.cms.repository.LanguageRepository;
import com.application.cms.service.LanguageService;
import com.application.cms.repository.search.LanguageSearchRepository;
import com.application.cms.service.dto.LanguageDTO;
import com.application.cms.service.mapper.LanguageMapper;

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
 * Test class for the LanguageResource REST controller.
 *
 * @see LanguageResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CmsApp.class)
public class LanguageResourceIntTest {
    private static final String DEFAULT_LABEL = "AAAAA";
    private static final String UPDATED_LABEL = "BBBBB";

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private LanguageMapper languageMapper;

    @Inject
    private LanguageService languageService;

    @Inject
    private LanguageSearchRepository languageSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restLanguageMockMvc;

    private Language language;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LanguageResource languageResource = new LanguageResource();
        ReflectionTestUtils.setField(languageResource, "languageService", languageService);
        this.restLanguageMockMvc = MockMvcBuilders.standaloneSetup(languageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Language createEntity(EntityManager em) {
        Language language = new Language();
        language = new Language()
                .label(DEFAULT_LABEL);
        return language;
    }

    @Before
    public void initTest() {
        languageSearchRepository.deleteAll();
        language = createEntity(em);
    }

    @Test
    @Transactional
    public void createLanguage() throws Exception {
        int databaseSizeBeforeCreate = languageRepository.findAll().size();

        // Create the Language
        LanguageDTO languageDTO = languageMapper.languageToLanguageDTO(language);

        restLanguageMockMvc.perform(post("/api/languages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(languageDTO)))
                .andExpect(status().isCreated());

        // Validate the Language in the database
        List<Language> languages = languageRepository.findAll();
        assertThat(languages).hasSize(databaseSizeBeforeCreate + 1);
        Language testLanguage = languages.get(languages.size() - 1);
        assertThat(testLanguage.getLabel()).isEqualTo(DEFAULT_LABEL);

        // Validate the Language in ElasticSearch
        Language languageEs = languageSearchRepository.findOne(testLanguage.getId());
        assertThat(languageEs).isEqualToComparingFieldByField(testLanguage);
    }

    @Test
    @Transactional
    public void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = languageRepository.findAll().size();
        // set the field null
        language.setLabel(null);

        // Create the Language, which fails.
        LanguageDTO languageDTO = languageMapper.languageToLanguageDTO(language);

        restLanguageMockMvc.perform(post("/api/languages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(languageDTO)))
                .andExpect(status().isBadRequest());

        List<Language> languages = languageRepository.findAll();
        assertThat(languages).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLanguages() throws Exception {
        // Initialize the database
        languageRepository.saveAndFlush(language);

        // Get all the languages
        restLanguageMockMvc.perform(get("/api/languages?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(language.getId().intValue())))
                .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }

    @Test
    @Transactional
    public void getLanguage() throws Exception {
        // Initialize the database
        languageRepository.saveAndFlush(language);

        // Get the language
        restLanguageMockMvc.perform(get("/api/languages/{id}", language.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(language.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLanguage() throws Exception {
        // Get the language
        restLanguageMockMvc.perform(get("/api/languages/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLanguage() throws Exception {
        // Initialize the database
        languageRepository.saveAndFlush(language);
        languageSearchRepository.save(language);
        int databaseSizeBeforeUpdate = languageRepository.findAll().size();

        // Update the language
        Language updatedLanguage = languageRepository.findOne(language.getId());
        updatedLanguage
                .label(UPDATED_LABEL);
        LanguageDTO languageDTO = languageMapper.languageToLanguageDTO(updatedLanguage);

        restLanguageMockMvc.perform(put("/api/languages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(languageDTO)))
                .andExpect(status().isOk());

        // Validate the Language in the database
        List<Language> languages = languageRepository.findAll();
        assertThat(languages).hasSize(databaseSizeBeforeUpdate);
        Language testLanguage = languages.get(languages.size() - 1);
        assertThat(testLanguage.getLabel()).isEqualTo(UPDATED_LABEL);

        // Validate the Language in ElasticSearch
        Language languageEs = languageSearchRepository.findOne(testLanguage.getId());
        assertThat(languageEs).isEqualToComparingFieldByField(testLanguage);
    }

    @Test
    @Transactional
    public void deleteLanguage() throws Exception {
        // Initialize the database
        languageRepository.saveAndFlush(language);
        languageSearchRepository.save(language);
        int databaseSizeBeforeDelete = languageRepository.findAll().size();

        // Get the language
        restLanguageMockMvc.perform(delete("/api/languages/{id}", language.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean languageExistsInEs = languageSearchRepository.exists(language.getId());
        assertThat(languageExistsInEs).isFalse();

        // Validate the database is empty
        List<Language> languages = languageRepository.findAll();
        assertThat(languages).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLanguage() throws Exception {
        // Initialize the database
        languageRepository.saveAndFlush(language);
        languageSearchRepository.save(language);

        // Search the language
        restLanguageMockMvc.perform(get("/api/_search/languages?query=id:" + language.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(language.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }
}
