package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT{

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/f3d5676d-d72b-42a4-9ba8-48f9a151b356"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234300019"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/f3d5676d-d72b-42a4-9ba8-48f9a151b356")
                        .header("Api-key", "spring")
                        .header("Api-secret", "guru"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerBadCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/f3d5676d-d72b-42a4-9ba8-48f9a151b356")
                        .header("Api-key", "spring")
                        .header("Api-secret", "bad-pass"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerUrlAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/f3d5676d-d72b-42a4-9ba8-48f9a151b356")
                        .param("Api-key", "spring")
                        .param("Api-secret", "guru"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerUrlAuthBadCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/f3d5676d-d72b-42a4-9ba8-48f9a151b356")
                        .param("Api-key", "spring")
                        .param("Api-secret", "bad-pass"))
                .andExpect(status().isUnauthorized());
    }
}
