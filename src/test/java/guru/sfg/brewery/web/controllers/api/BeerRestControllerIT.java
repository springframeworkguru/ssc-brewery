package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {
        public Beer beerToDelete() {
            Random rand = new Random();

            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete me beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(11)
                    .quantityToBrew(200)
                    .upc(String.valueOf(rand.nextInt(99999999)))
                    .build());
        }

        @Test
        void deleteBeer() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .header("Api-key", "spring")
                            .header("Api-secret", "guru"))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteBeerAdminRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void deleteBeerUserRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerCustomerRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerNoAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteBeerBadCreds() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .header("Api-key", "spring")
                            .header("Api-secret", "bad-pass"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteBeerUrlAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .param("Api-key", "spring")
                            .param("Api-secret", "guru"))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteBeerUrlAuthBadCreds() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .param("Api-key", "spring")
                            .param("Api-secret", "bad-pass"))
                    .andExpect(status().isUnauthorized());
        }

    }

    @DisplayName("View Beer tests")
    @Nested
    class viewBeerTests {
        @Test
        void findBeersAdminRole() throws Exception {
            mockMvc.perform(get("/api/v1/beer").with(
                            httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeersUserRole() throws Exception {
            mockMvc.perform(get("/api/v1/beer").with(
                            httpBasic("user", "password")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeersCustomerRole() throws Exception {
            mockMvc.perform(get("/api/v1/beer").with(
                            httpBasic("scott", "tiger")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeersWithoutAuth() throws Exception {
            mockMvc.perform(get("/api/v1/beer"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void findBeerByIdAdminRole() throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeerByIdCustomerRole() throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                            .with(httpBasic("scott", "tiger")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeerByIdUserRole() throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                            .with(httpBasic("user", "password")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeerByIdWithoutAuth() throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void findBeerByUpcAdminRole() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234300019")
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeerByUpcCustomerRole() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234300019")
                            .with(httpBasic("scott", "tiger")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeerByUpcUserRole() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234300019")
                            .with(httpBasic("user", "password")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void findBeerByUpcWithoutAuth() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234300019"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void listBreweryWithCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweryWithAdminRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweryWithUserRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBreweryWithNoAuth() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());
    }
}
