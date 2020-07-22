package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest
public class BeerControllerIT extends AbstractBaseIT {

    @Test
    void findBeerWithoutAuthorizedUser() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser("any random user")
    @Test
    void findBeerWithAuthorizedUSer() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeerWithWrongHttpBasicAuth() throws Exception {
        mockMvc.perform(get("/beers/find").with(httpBasic("random", "wrong pass")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerWithHttpBasicAuth() throws Exception {
        mockMvc.perform(get("/beers/find").with(httpBasic(user, password)))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

}
