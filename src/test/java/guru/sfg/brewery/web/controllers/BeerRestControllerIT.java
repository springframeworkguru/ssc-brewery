package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class BeerRestControllerIT extends AbstractBaseIT {

    @Test
    void findBeerWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerWithAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/beer/")
                .with(anonymous()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByIdWithAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                .with(anonymous()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/97df0c39-90c4-4ae0-b663-453e8e19c311"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpcWithAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/97df0c39-90c4-4ae0-b663-453e8e19c311")
                .with(anonymous()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerWithLegacyAuthentication() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                .header("api-key", user)
                .header("api-secret", password))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerWithLegacyAuthenticationWithWrongCredentials() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                .header("api-key", user)
                .header("api-secret", "some other secret"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerWithHttpBasicAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                .with(httpBasic(user, password)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerWithNoAuthFails() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311"))
                .andExpect(status().isUnauthorized());
    }

}
