package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class BeerRestControllerIT extends BaseIT {

  @Test
  void findBeers() throws Exception {
    mockMvc.perform(get("/api/v1/beer/"))
        .andExpect(status().isOk());
  }

  @Test
  void findBeerById() throws Exception {
    mockMvc.perform(get("/api/v1/beer/0f5086a4-c547-4a59-9d51-fb9dad5a84ed"))
        .andExpect(status().isOk());
  } //

  @Test
  void findBeerByUpc() throws Exception {
    mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
        .andExpect(status().isOk());
  }
}