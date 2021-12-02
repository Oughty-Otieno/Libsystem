package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Borrowing;
import com.mycompany.myapp.repository.BorrowingRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Borrowing}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BorrowingResource {

    private final Logger log = LoggerFactory.getLogger(BorrowingResource.class);

    private static final String ENTITY_NAME = "borrowing";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BorrowingRepository borrowingRepository;

    public BorrowingResource(BorrowingRepository borrowingRepository) {
        this.borrowingRepository = borrowingRepository;
    }

    /**
     * {@code POST  /borrowings} : Create a new borrowing.
     *
     * @param borrowing the borrowing to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new borrowing, or with status {@code 400 (Bad Request)} if the borrowing has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/borrowings")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Borrowing> createBorrowing(@RequestBody Borrowing borrowing) throws URISyntaxException {
        log.debug("REST request to save Borrowing : {}", borrowing);
        if (borrowing.getId() != null) {
            throw new BadRequestAlertException("A new borrowing cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Borrowing result = borrowingRepository.save(borrowing);
        return ResponseEntity
            .created(new URI("/api/borrowings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /borrowings/:id} : Updates an existing borrowing.
     *
     * @param id the id of the borrowing to save.
     * @param borrowing the borrowing to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrowing,
     * or with status {@code 400 (Bad Request)} if the borrowing is not valid,
     * or with status {@code 500 (Internal Server Error)} if the borrowing couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/borrowings/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Borrowing> updateBorrowing(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Borrowing borrowing
    ) throws URISyntaxException {
        log.debug("REST request to update Borrowing : {}, {}", id, borrowing);
        if (borrowing.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrowing.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Borrowing result = borrowingRepository.save(borrowing);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, borrowing.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /borrowings/:id} : Partial updates given fields of an existing borrowing, field will ignore if it is null
     *
     * @param id the id of the borrowing to save.
     * @param borrowing the borrowing to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrowing,
     * or with status {@code 400 (Bad Request)} if the borrowing is not valid,
     * or with status {@code 404 (Not Found)} if the borrowing is not found,
     * or with status {@code 500 (Internal Server Error)} if the borrowing couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/borrowings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Borrowing> partialUpdateBorrowing(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Borrowing borrowing
    ) throws URISyntaxException {
        log.debug("REST request to partial update Borrowing partially : {}, {}", id, borrowing);
        if (borrowing.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrowing.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Borrowing> result = borrowingRepository
            .findById(borrowing.getId())
            .map(existingBorrowing -> {
                if (borrowing.getDate_borrowed() != null) {
                    existingBorrowing.setDate_borrowed(borrowing.getDate_borrowed());
                }
                if (borrowing.getDue_date() != null) {
                    existingBorrowing.setDue_date(borrowing.getDue_date());
                }
                if (borrowing.getReturn_date() != null) {
                    existingBorrowing.setReturn_date(borrowing.getReturn_date());
                }
                if (borrowing.getStatus() != null) {
                    existingBorrowing.setStatus(borrowing.getStatus());
                }

                return existingBorrowing;
            })
            .map(borrowingRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, borrowing.getId().toString())
        );
    }

    /**
     * {@code GET  /borrowings} : get all the borrowings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of borrowings in body.
     */
    @GetMapping("/borrowings")
    public ResponseEntity<List<Borrowing>> getAllBorrowings(Pageable pageable) {
        log.debug("REST request to get a page of Borrowings");
        if(SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            Page<Borrowing> page = borrowingRepository.findAll(pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } else {
            Optional <String> usernameOptional = SecurityUtils.getCurrentUserLogin();
            if(usernameOptional.isPresent()) {
                Page<Borrowing> page = borrowingRepository.findByUserIsCurrentUser(usernameOptional.get(), pageable);
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(page.getContent());
            }
        }
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * {@code GET  /borrowings/:id} : get the "id" borrowing.
     *
     * @param id the id of the borrowing to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the borrowing, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/borrowings/{id}")
    public ResponseEntity<Borrowing> getBorrowing(@PathVariable Long id) {
        log.debug("REST request to get Borrowing : {}", id);
        Optional<Borrowing> borrowing = borrowingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(borrowing);
    }

    /**
     * {@code DELETE  /borrowings/:id} : delete the "id" borrowing.
     *
     * @param id the id of the borrowing to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/borrowings/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long id) {
        log.debug("REST request to delete Borrowing : {}", id);
        borrowingRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
