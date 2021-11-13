package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Spaces;
import com.mycompany.myapp.repository.SpacesRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Spaces}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SpacesResource {

    private final Logger log = LoggerFactory.getLogger(SpacesResource.class);

    private static final String ENTITY_NAME = "spaces";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpacesRepository spacesRepository;

    public SpacesResource(SpacesRepository spacesRepository) {
        this.spacesRepository = spacesRepository;
    }

    /**
     * {@code POST  /spaces} : Create a new spaces.
     *
     * @param spaces the spaces to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new spaces, or with status {@code 400 (Bad Request)} if the spaces has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/spaces")
    public ResponseEntity<Spaces> createSpaces(@RequestBody Spaces spaces) throws URISyntaxException {
        log.debug("REST request to save Spaces : {}", spaces);
        if (spaces.getId() != null) {
            throw new BadRequestAlertException("A new spaces cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Spaces result = spacesRepository.save(spaces);
        return ResponseEntity
            .created(new URI("/api/spaces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /spaces/:id} : Updates an existing spaces.
     *
     * @param id the id of the spaces to save.
     * @param spaces the spaces to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spaces,
     * or with status {@code 400 (Bad Request)} if the spaces is not valid,
     * or with status {@code 500 (Internal Server Error)} if the spaces couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/spaces/{id}")
    public ResponseEntity<Spaces> updateSpaces(@PathVariable(value = "id", required = false) final Long id, @RequestBody Spaces spaces)
        throws URISyntaxException {
        log.debug("REST request to update Spaces : {}, {}", id, spaces);
        if (spaces.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spaces.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spacesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Spaces result = spacesRepository.save(spaces);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spaces.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /spaces/:id} : Partial updates given fields of an existing spaces, field will ignore if it is null
     *
     * @param id the id of the spaces to save.
     * @param spaces the spaces to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spaces,
     * or with status {@code 400 (Bad Request)} if the spaces is not valid,
     * or with status {@code 404 (Not Found)} if the spaces is not found,
     * or with status {@code 500 (Internal Server Error)} if the spaces couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/spaces/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Spaces> partialUpdateSpaces(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Spaces spaces
    ) throws URISyntaxException {
        log.debug("REST request to partial update Spaces partially : {}, {}", id, spaces);
        if (spaces.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spaces.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spacesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Spaces> result = spacesRepository
            .findById(spaces.getId())
            .map(existingSpaces -> {
                if (spaces.getDate() != null) {
                    existingSpaces.setDate(spaces.getDate());
                }

                return existingSpaces;
            })
            .map(spacesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spaces.getId().toString())
        );
    }

    /**
     * {@code GET  /spaces} : get all the spaces.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spaces in body.
     */
    @GetMapping("/spaces")
    public ResponseEntity<List<Spaces>> getAllSpaces(Pageable pageable) {
        log.debug("REST request to get a page of Spaces");
        Page<Spaces> page = spacesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /spaces/:id} : get the "id" spaces.
     *
     * @param id the id of the spaces to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spaces, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/spaces/{id}")
    public ResponseEntity<Spaces> getSpaces(@PathVariable Long id) {
        log.debug("REST request to get Spaces : {}", id);
        Optional<Spaces> spaces = spacesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(spaces);
    }

    /**
     * {@code DELETE  /spaces/:id} : delete the "id" spaces.
     *
     * @param id the id of the spaces to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/spaces/{id}")
    public ResponseEntity<Void> deleteSpaces(@PathVariable Long id) {
        log.debug("REST request to delete Spaces : {}", id);
        spacesRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
