package guru.sfg.brewery.web.controllers;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CustomerControllerIT extends AbstractBaseIT {

    private static @NotNull Stream<Arguments> adminAndCustomer() {
        return Stream.of(
                Arguments.of("spring", "secret"),
                Arguments.of("scott", "tiger")
        );
    }

    @ParameterizedTest
    @MethodSource("adminAndCustomer")
    void listCustomersWithAuthorizedUser(@NotNull String user, @NotNull String password) throws Exception {
        mockMvc.perform(get("/customers")
                .with(httpBasic(user, password)))
                .andExpect(status().isOk());
    }

    @Test
    void listCustomersWithNoAuth() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listCustomersWithUnauthorizedUser() throws Exception {
        mockMvc.perform(get("/customers")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createNewCustomerWithNoAuth() throws Exception {
        mockMvc.perform(post("/customers/new"))
                .andExpect(status().isUnauthorized());
    }

    private static @NotNull Stream<Arguments> userAndCustomer() {
        return Stream.of(
                Arguments.of("user", "password"),
                Arguments.of("scott", "tiger")
        );
    }

    @ParameterizedTest
    @MethodSource("userAndCustomer")
    void createNewCustomerWithNoAdminAuth(@NotNull String user, @NotNull String password) throws Exception {
        mockMvc.perform(post("/customers/new")
                .with(httpBasic(user, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createNewCustomerWithAdminAuth() throws Exception {
        mockMvc.perform(post("/customers/new")
                .with(httpBasic(adminUser, adminPassword)))
                .andExpect(status().is3xxRedirection());
    }

}
