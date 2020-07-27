package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends AbstractBaseIT {

    @Autowired
    BeerRepository beerRepository;

    private @NotNull String getBeerId() {
        return beerRepository.findAll().stream()
                .findAny()
                .map(Beer::getId)
                .map(UUID::toString)
                .orElseThrow(() -> new UnsupportedOperationException("There may be no beers in DB."));
    }

    private @NotNull String getBeerUpc() {
        return beerRepository.findAll().stream()
                .findAny()
                .map(Beer::getUpc)
                .orElseThrow(() -> new UnsupportedOperationException("There may be no beers in DB."));
    }

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
        mockMvc.perform(get("/api/v1/beer/" + getBeerId()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerWithAdminRole() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "")
                .with(httpBasic(adminUser, adminPassword)))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByIdWithAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/beer/" + getBeerId())
                .with(anonymous()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/" + getBeerUpc()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpcWithAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/" + getBeerUpc())
                .with(anonymous()))
                .andExpect(status().isOk());
    }

    @Nested
    class DeleteTests {

        Beer beerToDelete() {
            String upc = String.valueOf(new Random().nextInt());

            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete Me Beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(upc)
                    .build());
        }

        @Test
        void deleteBeerWithCustomerRoleIsForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerWithUserRoleIsForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerWithNoAuthFails() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }

    }

}
