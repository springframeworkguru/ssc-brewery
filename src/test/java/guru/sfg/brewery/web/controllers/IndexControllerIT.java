package guru.sfg.brewery.web.controllers;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class IndexControllerIT extends AbstractBaseIT {

    private static @NotNull Stream<String> publicUrls() {
        return Stream.of("/", "/login");
    }

    @ParameterizedTest
    @MethodSource("publicUrls")
    void getPublicUrlWithoutHttpBasicAuth(@NotNull String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

}
