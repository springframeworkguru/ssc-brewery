package guru.sfg.brewery.misc;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Playing around with different implementations of {@link PasswordEncoder}.
 * There is no value in these tests, except educational (like the whole project).
 */
public class PasswordEncoderTest {

    private static final String PLAIN_PASSWORD = "password";

    @Test
    void testNoopEncoder() {
        assertEquals(PLAIN_PASSWORD, NoOpPasswordEncoder.getInstance().encode(PLAIN_PASSWORD));
    }

    @Test
    void testDigest() {
        assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", DigestUtils.md5DigestAsHex(PLAIN_PASSWORD.getBytes()));
    }

    private static @NotNull Stream<PasswordEncoder> encoders() {
        return Stream.of(new LdapShaPasswordEncoder(), new StandardPasswordEncoder(), new BCryptPasswordEncoder());
    }

    @ParameterizedTest
    @MethodSource("encoders")
    void testDifferentEncoders(@NotNull PasswordEncoder encoder) {
        String encodedPassword = encoder.encode(PLAIN_PASSWORD);
        assertTrue(encoder.matches(PLAIN_PASSWORD, encodedPassword));

        for (int i = 0; i < 3; i++) {
            String tmpEncodedPass = encoder.encode(PLAIN_PASSWORD);
            assertTrue(encoder.matches(PLAIN_PASSWORD, tmpEncodedPass));
            assertNotEquals(encodedPassword, tmpEncodedPass);
        }
    }

    private static @NotNull Stream<Integer> upToTen() {
        return IntStream.range(4, 14).boxed();
    }

    @ParameterizedTest
    @MethodSource("upToTen")
    void bCryptStartsWith(int strength) {
        String encodedPassword = new BCryptPasswordEncoder(strength).encode(PLAIN_PASSWORD);
        String expectedStart = String.format("$2a$%02d$", strength);

        assertTrue(encodedPassword.startsWith(expectedStart),
                String.format("Encoded password was expected to start with [%s] but actual is [%s]",
                        expectedStart, encodedPassword));
    }

}
