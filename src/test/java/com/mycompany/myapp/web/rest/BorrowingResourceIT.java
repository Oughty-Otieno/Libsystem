package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Borrowing;
import com.mycompany.myapp.repository.BorrowingRepository;
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
 * Integration tests for the {@link BorrowingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BorrowingResourceIT {

    private static final LocalDate DEFAULT_DATE_BORROWED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_BORROWED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_RETURN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RETURN_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/borrowings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBorrowingMockMvc;

    private Borrowing borrowing;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Borrowing createEntity(EntityManager em) {
        Borrowing borrowing = new Borrowing()
            .date_borrowed(DEFAULT_DATE_BORROWED)
            .due_date(DEFAULT_DUE_DATE)
            .return_date(DEFAULT_RETURN_DATE)
            .status(DEFAULT_STATUS);
        return borrowing;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Borrowing createUpdatedEntity(EntityManager em) {
        Borrowing borrowing = new Borrowing()
            .date_borrowed(UPDATED_DATE_BORROWED)
            .due_date(UPDATED_DUE_DATE)
            .return_date(UPDATED_RETURN_DATE)
            .status(UPDATED_STATUS);
        return borrowing;
    }

    @BeforeEach
    public void initTest() {
        borrowing = createEntity(em);
    }

    @Test
    @Transactional
    void createBorrowing() throws Exception {
        int databaseSizeBeforeCreate = borrowingRepository.findAll().size();
        // Create the Borrowing
        restBorrowingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrowing)))
            .andExpect(status().isCreated());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeCreate + 1);
        Borrowing testBorrowing = borrowingList.get(borrowingList.size() - 1);
        assertThat(testBorrowing.getDate_borrowed()).isEqualTo(DEFAULT_DATE_BORROWED);
        assertThat(testBorrowing.getDue_date()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testBorrowing.getReturn_date()).isEqualTo(DEFAULT_RETURN_DATE);
        assertThat(testBorrowing.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createBorrowingWithExistingId() throws Exception {
        // Create the Borrowing with an existing ID
        borrowing.setId(1L);

        int databaseSizeBeforeCreate = borrowingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBorrowingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrowing)))
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBorrowings() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList
        restBorrowingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrowing.getId().intValue())))
            .andExpect(jsonPath("$.[*].date_borrowed").value(hasItem(DEFAULT_DATE_BORROWED.toString())))
            .andExpect(jsonPath("$.[*].due_date").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].return_date").value(hasItem(DEFAULT_RETURN_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())));
    }

    @Test
    @Transactional
    void getBorrowing() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get the borrowing
        restBorrowingMockMvc
            .perform(get(ENTITY_API_URL_ID, borrowing.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(borrowing.getId().intValue()))
            .andExpect(jsonPath("$.date_borrowed").value(DEFAULT_DATE_BORROWED.toString()))
            .andExpect(jsonPath("$.due_date").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.return_date").value(DEFAULT_RETURN_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingBorrowing() throws Exception {
        // Get the borrowing
        restBorrowingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBorrowing() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();

        // Update the borrowing
        Borrowing updatedBorrowing = borrowingRepository.findById(borrowing.getId()).get();
        // Disconnect from session so that the updates on updatedBorrowing are not directly saved in db
        em.detach(updatedBorrowing);
        updatedBorrowing
            .date_borrowed(UPDATED_DATE_BORROWED)
            .due_date(UPDATED_DUE_DATE)
            .return_date(UPDATED_RETURN_DATE)
            .status(UPDATED_STATUS);

        restBorrowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBorrowing.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBorrowing))
            )
            .andExpect(status().isOk());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
        Borrowing testBorrowing = borrowingList.get(borrowingList.size() - 1);
        assertThat(testBorrowing.getDate_borrowed()).isEqualTo(UPDATED_DATE_BORROWED);
        assertThat(testBorrowing.getDue_date()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testBorrowing.getReturn_date()).isEqualTo(UPDATED_RETURN_DATE);
        assertThat(testBorrowing.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, borrowing.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrowing)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBorrowingWithPatch() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();

        // Update the borrowing using partial update
        Borrowing partialUpdatedBorrowing = new Borrowing();
        partialUpdatedBorrowing.setId(borrowing.getId());

        partialUpdatedBorrowing.due_date(UPDATED_DUE_DATE).return_date(UPDATED_RETURN_DATE);

        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrowing.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBorrowing))
            )
            .andExpect(status().isOk());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
        Borrowing testBorrowing = borrowingList.get(borrowingList.size() - 1);
        assertThat(testBorrowing.getDate_borrowed()).isEqualTo(DEFAULT_DATE_BORROWED);
        assertThat(testBorrowing.getDue_date()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testBorrowing.getReturn_date()).isEqualTo(UPDATED_RETURN_DATE);
        assertThat(testBorrowing.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateBorrowingWithPatch() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();

        // Update the borrowing using partial update
        Borrowing partialUpdatedBorrowing = new Borrowing();
        partialUpdatedBorrowing.setId(borrowing.getId());

        partialUpdatedBorrowing
            .date_borrowed(UPDATED_DATE_BORROWED)
            .due_date(UPDATED_DUE_DATE)
            .return_date(UPDATED_RETURN_DATE)
            .status(UPDATED_STATUS);

        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrowing.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBorrowing))
            )
            .andExpect(status().isOk());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
        Borrowing testBorrowing = borrowingList.get(borrowingList.size() - 1);
        assertThat(testBorrowing.getDate_borrowed()).isEqualTo(UPDATED_DATE_BORROWED);
        assertThat(testBorrowing.getDue_date()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testBorrowing.getReturn_date()).isEqualTo(UPDATED_RETURN_DATE);
        assertThat(testBorrowing.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, borrowing.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBorrowing() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        int databaseSizeBeforeDelete = borrowingRepository.findAll().size();

        // Delete the borrowing
        restBorrowingMockMvc
            .perform(delete(ENTITY_API_URL_ID, borrowing.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
