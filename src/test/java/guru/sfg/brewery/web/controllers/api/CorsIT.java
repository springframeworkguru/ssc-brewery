package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by jt on 7/28/20.
 */
@SpringBootTest
public class CorsIT extends BaseIT {
    @WithUserDetails("spring")
    @Test
    void findBeersAUTH() throws Exception {
        mockMvc.perform(get("/api/v1/beer/")
                .header("Origin", "https://springframework.guru"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void findBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beer/")
                .header("Origin", "https://springframework.guru")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void postBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beer/")
                .header("Origin", "https://springframework.guru")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void putBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beer/1234")
                .header("Origin", "https://springframework.guru")
                .header("Access-Control-Request-Method", "PUT"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void deleteBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beer/1234")
                .header("Origin", "https://springframework.guru")
                .header("Access-Control-Request-Method", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

}
