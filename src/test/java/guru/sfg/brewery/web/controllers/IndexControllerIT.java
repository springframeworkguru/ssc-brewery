package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Modified by Pierrot on 2023-01-22.
 */
public class IndexControllerIT extends BaseIT {

    @Test
    void testGetIndexSlash() throws Exception{
        mockMvc.perform(get("/" ))
                .andExpect(status().isOk());
    }
}
