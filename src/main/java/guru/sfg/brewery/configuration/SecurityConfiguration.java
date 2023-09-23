package guru.sfg.brewery.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/resources/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/beer/**", GET.name())).permitAll()
                        .mvcMatchers(GET, "/api/v1/beerUpc/{upc}").permitAll()
                )
                .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .formLogin(withDefaults())
                .logout(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("grayroom")
                .password("secret")
                .roles("ADMIN")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

}
