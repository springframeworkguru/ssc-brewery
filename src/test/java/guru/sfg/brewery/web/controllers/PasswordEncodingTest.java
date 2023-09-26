package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncodingTest {

    static final String PASSWORD = "password";

    @Test
    void hashingExample() {
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));

        String salted = PASSWORD + "ThisIsMySALTVALUE";
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
    }

    @Test
    void NoOpEncoder() {
        PasswordEncoder noOpEncoder = NoOpPasswordEncoder.getInstance();

        System.out.println(noOpEncoder.encode(PASSWORD));
        System.out.println(noOpEncoder.encode(PASSWORD));
    }

    @Test
    void LdapEncoder() {
        PasswordEncoder ldapEncoder = new LdapShaPasswordEncoder();

        System.out.println(ldapEncoder.encode(PASSWORD));
        System.out.println(ldapEncoder.encode(PASSWORD));

        assertTrue(ldapEncoder.matches(PASSWORD, ldapEncoder.encode(PASSWORD)));
    }

}
