package guru.sfg.brewery.security;

import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(@Nullable String username) throws UsernameNotFoundException {
        log.debug("Getting authentication data via JPA for user {}.", username);

        if (StringUtils.isEmpty(username)) {
            return null;
        }

        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User [%s] was not found.", username)));
    }

}
