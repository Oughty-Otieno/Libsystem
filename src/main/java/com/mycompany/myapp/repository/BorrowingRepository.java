package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Borrowing;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Borrowing entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    @Query("select borrowing from Borrowing borrowing where borrowing.user.login = ?#{principal.username}")
    List<Borrowing> findByUserIsCurrentUser();
}
