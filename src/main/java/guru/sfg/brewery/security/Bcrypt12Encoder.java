package guru.sfg.brewery.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Bcrypt12Encoder implements PasswordEncoder {
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public Bcrypt12Encoder() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
