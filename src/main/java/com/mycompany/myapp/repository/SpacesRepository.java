package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Borrowing;
import com.mycompany.myapp.domain.Spaces;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Spaces entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpacesRepository extends JpaRepository<Spaces, Long> {
    @Query("select spaces from Spaces spaces where spaces.user.login = :username")
    Page<Spaces> findByUserIsCurrentUser(@Param("username")String currentUser, Pageable pageable);
}
