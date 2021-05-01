package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.BeerInventory;
import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class BeerControllerIT {

  @Autowired
  WebApplicationContext wac;

  MockMvc mockMvc;

  @MockBean
  BeerRepository beerRepository;

  @MockBean
  BeerInventoryRepository beerInventoryRepository;

  @MockBean
  BreweryService breweryService;

  @MockBean
  CustomerRepository customerRepository;

  @MockBean
  BeerService beerService;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac)
        // this enables spring security filters
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .build();
  }

  // this test will pass security with any user filled bellow,
  // it has nothing to do with what was config in application.properties
  @WithMockUser("spring")
  @Test
  void findBeers() throws Exception {
    mockMvc.perform(get("/beers/find"))
        .andExpect(status().isOk())
        .andExpect(view().name("beers/findBeers"))
        .andExpect(model().attributeExists("beer"));
  }

  @Test
  void findBeersWithHttpBasic() throws Exception {
    mockMvc.perform(get("/beers/find").with(httpBasic("spring", "guru")))
        .andExpect(status().isOk())
        .andExpect(view().name("beers/findBeers"))
        .andExpect(model().attributeExists("beer"));
  }
}
