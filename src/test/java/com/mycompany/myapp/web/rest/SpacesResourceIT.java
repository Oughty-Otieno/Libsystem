package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Spaces;
import com.mycompany.myapp.repository.SpacesRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SpacesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SpacesResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/spaces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpacesRepository spacesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpacesMockMvc;

    private Spaces spaces;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Spaces createEntity(EntityManager em) {
        Spaces spaces = new Spaces().date(DEFAULT_DATE);
        return spaces;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Spaces createUpdatedEntity(EntityManager em) {
        Spaces spaces = new Spaces().date(UPDATED_DATE);
        return spaces;
    }

    @BeforeEach
    public void initTest() {
        spaces = createEntity(em);
    }

    @Test
    @Transactional
    void createSpaces() throws Exception {
        int databaseSizeBeforeCreate = spacesRepository.findAll().size();
        // Create the Spaces
        restSpacesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaces)))
            .andExpect(status().isCreated());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeCreate + 1);
        Spaces testSpaces = spacesList.get(spacesList.size() - 1);
        assertThat(testSpaces.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createSpacesWithExistingId() throws Exception {
        // Create the Spaces with an existing ID
        spaces.setId(1L);

        int databaseSizeBeforeCreate = spacesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpacesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaces)))
            .andExpect(status().isBadRequest());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSpaces() throws Exception {
        // Initialize the database
        spacesRepository.saveAndFlush(spaces);

        // Get all the spacesList
        restSpacesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spaces.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getSpaces() throws Exception {
        // Initialize the database
        spacesRepository.saveAndFlush(spaces);

        // Get the spaces
        restSpacesMockMvc
            .perform(get(ENTITY_API_URL_ID, spaces.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(spaces.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSpaces() throws Exception {
        // Get the spaces
        restSpacesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSpaces() throws Exception {
        // Initialize the database
        spacesRepository.saveAndFlush(spaces);

        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();

        // Update the spaces
        Spaces updatedSpaces = spacesRepository.findById(spaces.getId()).get();
        // Disconnect from session so that the updates on updatedSpaces are not directly saved in db
        em.detach(updatedSpaces);
        updatedSpaces.date(UPDATED_DATE);

        restSpacesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSpaces.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSpaces))
            )
            .andExpect(status().isOk());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
        Spaces testSpaces = spacesList.get(spacesList.size() - 1);
        assertThat(testSpaces.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingSpaces() throws Exception {
        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();
        spaces.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpacesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spaces.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spaces))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpaces() throws Exception {
        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();
        spaces.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpacesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spaces))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpaces() throws Exception {
        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();
        spaces.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpacesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaces)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpacesWithPatch() throws Exception {
        // Initialize the database
        spacesRepository.saveAndFlush(spaces);

        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();

        // Update the spaces using partial update
        Spaces partialUpdatedSpaces = new Spaces();
        partialUpdatedSpaces.setId(spaces.getId());

        restSpacesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpaces.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpaces))
            )
            .andExpect(status().isOk());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
        Spaces testSpaces = spacesList.get(spacesList.size() - 1);
        assertThat(testSpaces.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void fullUpdateSpacesWithPatch() throws Exception {
        // Initialize the database
        spacesRepository.saveAndFlush(spaces);

        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();

        // Update the spaces using partial update
        Spaces partialUpdatedSpaces = new Spaces();
        partialUpdatedSpaces.setId(spaces.getId());

        partialUpdatedSpaces.date(UPDATED_DATE);

        restSpacesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpaces.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpaces))
            )
            .andExpect(status().isOk());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
        Spaces testSpaces = spacesList.get(spacesList.size() - 1);
        assertThat(testSpaces.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingSpaces() throws Exception {
        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();
        spaces.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpacesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, spaces.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spaces))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpaces() throws Exception {
        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();
        spaces.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpacesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spaces))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpaces() throws Exception {
        int databaseSizeBeforeUpdate = spacesRepository.findAll().size();
        spaces.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpacesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(spaces)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Spaces in the database
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpaces() throws Exception {
        // Initialize the database
        spacesRepository.saveAndFlush(spaces);

        int databaseSizeBeforeDelete = spacesRepository.findAll().size();

        // Delete the spaces
        restSpacesMockMvc
            .perform(delete(ENTITY_API_URL_ID, spaces.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Spaces> spacesList = spacesRepository.findAll();
        assertThat(spacesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
