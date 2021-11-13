package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SpacesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Spaces.class);
        Spaces spaces1 = new Spaces();
        spaces1.setId(1L);
        Spaces spaces2 = new Spaces();
        spaces2.setId(spaces1.getId());
        assertThat(spaces1).isEqualTo(spaces2);
        spaces2.setId(2L);
        assertThat(spaces1).isNotEqualTo(spaces2);
        spaces1.setId(null);
        assertThat(spaces1).isNotEqualTo(spaces2);
    }
}
