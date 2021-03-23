package guru.sfg.brewery.web.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
public class IndexControllerIT extends BaseIT {

    @Test
    public void testGetIndexSlash() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
            .andExpect(status().isOk());
    }

}
