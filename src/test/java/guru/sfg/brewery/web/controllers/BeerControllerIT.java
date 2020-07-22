package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest
public class BeerControllerIT {

    @Value("${spring.security.user.name:}")
    String user;
    @Value("${spring.security.user.password:}")
    String password;

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    BeerRepository beerRepository;
    @MockBean
    BeerInventoryRepository beerInventoryRepository;
    @MockBean
    BeerService beerService;
    @MockBean
    BreweryService breweryService;
    @MockBean
    CustomerRepository customerRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

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
