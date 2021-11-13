package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BorrowingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Borrowing.class);
        Borrowing borrowing1 = new Borrowing();
        borrowing1.setId(1L);
        Borrowing borrowing2 = new Borrowing();
        borrowing2.setId(borrowing1.getId());
        assertThat(borrowing1).isEqualTo(borrowing2);
        borrowing2.setId(2L);
        assertThat(borrowing1).isNotEqualTo(borrowing2);
        borrowing1.setId(null);
        assertThat(borrowing1).isNotEqualTo(borrowing2);
    }
}
