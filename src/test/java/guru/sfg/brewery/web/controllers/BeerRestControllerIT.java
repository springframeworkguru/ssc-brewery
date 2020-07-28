package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerRestControllerIT extends AbstractBaseIT {

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

    private static @NotNull Stream<Arguments> usersAndPasswords() {
        return Stream.of(
                Arguments.of("scott", "tiger"),
                Arguments.of("user", "password"),
                Arguments.of("spring", "secret")
        );
    }

    @Test
    void findBeerWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerWithAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/beer/")
                .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("usersAndPasswords")
    void findBeerWithCredentials(@NotNull String user, @NotNull String password) throws Exception {
        mockMvc.perform(get("/api/v1/beer/")
                .with(httpBasic(user, password)))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByIdWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/beer/" + getBeerId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerByIdWithAnonymousAuth() throws Exception {
        mockMvc.perform(get("/api/v1/beer/" + getBeerId())
                .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("usersAndPasswords")
    void findBeerByIdWithCredentials(@NotNull String user, @NotNull String password) throws Exception {
        mockMvc.perform(get("/api/v1/beer/" + getBeerId())
                .with(httpBasic(user, password)))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersWithoutAuth() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeersWithAnonymousAuth() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "")
                .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("usersAndPasswords")
    void findBeersWithCredentials(@NotNull String user, @NotNull String password) throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "")
                .with(httpBasic(user, password)))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/" + getBeerUpc()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerByUpcWithAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/" + getBeerUpc())
                .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("usersAndPasswords")
    void findBeersByUpcWithCredentials(@NotNull String user, @NotNull String password) throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/" + getBeerUpc())
                .with(httpBasic(user, password)))
                .andExpect(status().isOk());
    }

    @Nested
    class DeleteBeerTests {

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
