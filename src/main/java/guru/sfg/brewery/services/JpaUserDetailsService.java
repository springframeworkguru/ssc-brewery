package guru.sfg.brewery.services;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // We are using @Transactional for executing this method in one Hibernate context so that we do not need
    // to configure user to load authorities eagerly (we could alternatively do that).
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(()-> {
            throw new UsernameNotFoundException("User name: " + username + " not found");
        });
        // User из пакета org.springframework.security.core.userdetails.User implements UserDetails interface and is
        // immutable.
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getEnabled(), user.getAccountNonExpired(), user.getCredentialsNonExpired(), user.getAccountNonLocked(),
                convertToSpringAuthorities(user.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
        if (authorities != null && authorities.size() > 0){
            return authorities.stream().map(Authority::getRole).map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }else {
            return new HashSet<>();
        }
    }
}
