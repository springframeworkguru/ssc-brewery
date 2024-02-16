package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class PasswordEncodingTest {
    static final String PASSWORD = "password";

    @Test
    void hashingExample() {
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));

        String salted = PASSWORD + "ThisIsASaltValue";
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
    }

    @Test
    void testNoOp() {
        PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();
        System.out.println(noOp.encode(PASSWORD));
    }

    @Test
    void testLdap() {
        PasswordEncoder ldap = new LdapShaPasswordEncoder();
        System.out.println(ldap.encode(PASSWORD));
        System.out.println(ldap.encode(PASSWORD));

        String encoded = ldap.encode(PASSWORD);
        assertTrue(ldap.matches(PASSWORD, encoded));
    }

    @Test
    void testSha256() {
        PasswordEncoder encoder = new StandardPasswordEncoder();
        System.out.println(encoder.encode(PASSWORD));
        System.out.println(encoder.encode("pass"));
    }

    @Test
    void testBcrypt() {
        PasswordEncoder encoder = new BCryptPasswordEncoder(12);
        System.out.println(encoder.encode(PASSWORD));
        System.out.println(encoder.encode(PASSWORD));
    }

    @Test
    void testPbk() {
        PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
        System.out.println(encoder.encode("tiger"));
        System.out.println(encoder.encode("tiger"));
    }
}
