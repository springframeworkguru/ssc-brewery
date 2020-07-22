package guru.sfg.brewery.web.controllers;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
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
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
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
    void findBeerWithAnonymous() throws Exception {
        mockMvc.perform(get("/beers/find").with(anonymous()))
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

    private static @NotNull Stream<Arguments> usersAndPasswords() {
        return Stream.of(
                Arguments.of("spring", "secret"),
                Arguments.of("user", "password")
        );
    }

    @ParameterizedTest
    @MethodSource("usersAndPasswords")
    void initCreationForm(@NotNull String user, @NotNull String password) throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic(user, password)))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

}
