package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class UserLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public void run(String... args) {
        loadUserData();
    }

    private void loadUserData() {

        Authority a1 = Authority.builder().role("ROLE_ADMIN").build();
        Authority a2 = Authority.builder().role("ROLE_USER").build();
        Authority a3 = Authority.builder().role("ROLE_CUSTOMER").build();

        User user1 = User.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .authority(a1)
                .build();

        User user2 = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .authority(a2)
                .build();

        User user3 = User.builder()
                .username("scott")
                .password(passwordEncoder.encode("password"))
                .authority(a3)
                .build();

        if (authorityRepository.count() == 0) {
            authorityRepository.save(a1);
            authorityRepository.save(a2);
            authorityRepository.save(a3);
        }


        if (userRepository.count() == 0) {
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
        }

    }
}
