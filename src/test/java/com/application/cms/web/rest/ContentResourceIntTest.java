package com.application.cms.web.rest;

import com.application.cms.CmsApp;
import com.application.cms.domain.Content;
import com.application.cms.repository.ContentRepository;
import com.application.cms.service.ContentService;
import com.application.cms.repository.search.ContentSearchRepository;
import com.application.cms.service.dto.ContentDTO;
import com.application.cms.service.mapper.ContentMapper;

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
 * Test class for the ContentResource REST controller.
 *
 * @see ContentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CmsApp.class)
public class ContentResourceIntTest {
    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";
    private static final String DEFAULT_ALIAS = "AAAAA";
    private static final String UPDATED_ALIAS = "BBBBB";
    private static final String DEFAULT_INTROTEXT = "AAAAA";
    private static final String UPDATED_INTROTEXT = "BBBBB";
    private static final String DEFAULT_ALLTEXT = "AAAAA";
    private static final String UPDATED_ALLTEXT = "BBBBB";

    @Inject
    private ContentRepository contentRepository;

    @Inject
    private ContentMapper contentMapper;

    @Inject
    private ContentService contentService;

    @Inject
    private ContentSearchRepository contentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restContentMockMvc;

    private Content content;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContentResource contentResource = new ContentResource();
        ReflectionTestUtils.setField(contentResource, "contentService", contentService);
        this.restContentMockMvc = MockMvcBuilders.standaloneSetup(contentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Content createEntity(EntityManager em) {
        Content content = new Content();
        content = new Content()
                .title(DEFAULT_TITLE)
                .alias(DEFAULT_ALIAS)
                .introtext(DEFAULT_INTROTEXT)
                .alltext(DEFAULT_ALLTEXT);
        return content;
    }

    @Before
    public void initTest() {
        contentSearchRepository.deleteAll();
        content = createEntity(em);
    }

    @Test
    @Transactional
    public void createContent() throws Exception {
        int databaseSizeBeforeCreate = contentRepository.findAll().size();

        // Create the Content
        ContentDTO contentDTO = contentMapper.contentToContentDTO(content);

        restContentMockMvc.perform(post("/api/contents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contentDTO)))
                .andExpect(status().isCreated());

        // Validate the Content in the database
        List<Content> contents = contentRepository.findAll();
        assertThat(contents).hasSize(databaseSizeBeforeCreate + 1);
        Content testContent = contents.get(contents.size() - 1);
        assertThat(testContent.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testContent.getAlias()).isEqualTo(DEFAULT_ALIAS);
        assertThat(testContent.getIntrotext()).isEqualTo(DEFAULT_INTROTEXT);
        assertThat(testContent.getAlltext()).isEqualTo(DEFAULT_ALLTEXT);

        // Validate the Content in ElasticSearch
        Content contentEs = contentSearchRepository.findOne(testContent.getId());
        assertThat(contentEs).isEqualToComparingFieldByField(testContent);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = contentRepository.findAll().size();
        // set the field null
        content.setTitle(null);

        // Create the Content, which fails.
        ContentDTO contentDTO = contentMapper.contentToContentDTO(content);

        restContentMockMvc.perform(post("/api/contents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contentDTO)))
                .andExpect(status().isBadRequest());

        List<Content> contents = contentRepository.findAll();
        assertThat(contents).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllContents() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contents
        restContentMockMvc.perform(get("/api/contents?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(content.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS.toString())))
                .andExpect(jsonPath("$.[*].introtext").value(hasItem(DEFAULT_INTROTEXT.toString())))
                .andExpect(jsonPath("$.[*].alltext").value(hasItem(DEFAULT_ALLTEXT.toString())));
    }

    @Test
    @Transactional
    public void getContent() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get the content
        restContentMockMvc.perform(get("/api/contents/{id}", content.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(content.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.alias").value(DEFAULT_ALIAS.toString()))
            .andExpect(jsonPath("$.introtext").value(DEFAULT_INTROTEXT.toString()))
            .andExpect(jsonPath("$.alltext").value(DEFAULT_ALLTEXT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingContent() throws Exception {
        // Get the content
        restContentMockMvc.perform(get("/api/contents/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContent() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);
        contentSearchRepository.save(content);
        int databaseSizeBeforeUpdate = contentRepository.findAll().size();

        // Update the content
        Content updatedContent = contentRepository.findOne(content.getId());
        updatedContent
                .title(UPDATED_TITLE)
                .alias(UPDATED_ALIAS)
                .introtext(UPDATED_INTROTEXT)
                .alltext(UPDATED_ALLTEXT);
        ContentDTO contentDTO = contentMapper.contentToContentDTO(updatedContent);

        restContentMockMvc.perform(put("/api/contents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contentDTO)))
                .andExpect(status().isOk());

        // Validate the Content in the database
        List<Content> contents = contentRepository.findAll();
        assertThat(contents).hasSize(databaseSizeBeforeUpdate);
        Content testContent = contents.get(contents.size() - 1);
        assertThat(testContent.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testContent.getAlias()).isEqualTo(UPDATED_ALIAS);
        assertThat(testContent.getIntrotext()).isEqualTo(UPDATED_INTROTEXT);
        assertThat(testContent.getAlltext()).isEqualTo(UPDATED_ALLTEXT);

        // Validate the Content in ElasticSearch
        Content contentEs = contentSearchRepository.findOne(testContent.getId());
        assertThat(contentEs).isEqualToComparingFieldByField(testContent);
    }

    @Test
    @Transactional
    public void deleteContent() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);
        contentSearchRepository.save(content);
        int databaseSizeBeforeDelete = contentRepository.findAll().size();

        // Get the content
        restContentMockMvc.perform(delete("/api/contents/{id}", content.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean contentExistsInEs = contentSearchRepository.exists(content.getId());
        assertThat(contentExistsInEs).isFalse();

        // Validate the database is empty
        List<Content> contents = contentRepository.findAll();
        assertThat(contents).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchContent() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);
        contentSearchRepository.save(content);

        // Search the content
        restContentMockMvc.perform(get("/api/_search/contents?query=id:" + content.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(content.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS.toString())))
            .andExpect(jsonPath("$.[*].introtext").value(hasItem(DEFAULT_INTROTEXT.toString())))
            .andExpect(jsonPath("$.[*].alltext").value(hasItem(DEFAULT_ALLTEXT.toString())));
    }
}
