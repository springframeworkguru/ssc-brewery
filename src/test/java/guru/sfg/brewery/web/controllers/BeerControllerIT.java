package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
public class BeerControllerIT extends BaseIT {

    @WithMockUser("spring")
    @Test
    public void findBeers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/find"))
            .andExpect(status().isOk())
            .andExpect(view().name("beers/findBeers"))
            .andExpect(model().attributeExists("beer"));
    }

    @Test
    public void findBeersWIthHttpBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/find").with(httpBasic("spring", "spring")))
            .andExpect(status().isOk())
            .andExpect(view().name("beers/findBeers"))
            .andExpect(model().attributeExists("beer"));
    }


}
