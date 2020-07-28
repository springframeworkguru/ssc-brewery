package guru.sfg.brewery.web.controllers;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BreweryRestControllerIT extends AbstractBaseIT {

    private static @NotNull Stream<String> urls() {
        return Stream.of("/brewery/api/v1/breweries", "/brewery/breweries");
    }

    @ParameterizedTest
    @MethodSource("urls")
    void getBreweriesWithNoAuth(@NotNull String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("urls")
    void getBreweriesAnonymouslyIsUnauthorized(@NotNull String url) throws Exception {
        mockMvc.perform(get(url)
                .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("urls")
    void getBreweriesWithCustomerRoleIsForbidden(@NotNull String url) throws Exception {
        mockMvc.perform(get(url)
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @ParameterizedTest
    @MethodSource("urls")
    void getBreweriesWithUserRoleIsForbidden(@NotNull String url) throws Exception {
        mockMvc.perform(get(url)
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("urls")
    void getBreweriesWithAdminRoleIsAllowed(@NotNull String url) throws Exception {
        mockMvc.perform(get(url)
                .with(httpBasic(adminUser, adminPassword)))
                .andExpect(status().is2xxSuccessful());
    }

}
