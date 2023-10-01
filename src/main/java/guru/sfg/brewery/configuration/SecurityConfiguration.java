package guru.sfg.brewery.configuration;

import guru.sfg.brewery.security.CustomAuthenticationFilter;
import guru.sfg.brewery.security.CustomEncoderFactories;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager) {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationFilter customAuthenticationFilter) throws Exception {

        http.addFilterBefore(customAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
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

        //h2 configuration
        http.headers().frameOptions().sameOrigin();

        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("grayroom")
//                .password("secret")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }


//    @Autowired
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(jpaUserDetailsService).passwordEncoder(passwordEncoder());
//
//        auth.inMemoryAuthentication()
//                .withUser("grayroom")
//                .password("{bcrypt}$2a$10$EVWcixyQvs2nXw8sW0CmwejBWguUdUzPch4dlqKOq1t2LCCv3si.W")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{sha256}32186cdcfa7455485c7f96312935f466e097bbe9bd5a0c7a5800629bed0404a8de3c9a4a0f67695c")
//                .roles("USER")
//                .and()
//                .withUser("scott")
//                .password("{ldap}{SSHA}9lbt6Ru2e27T935DRXUUWhSzZhnYdBK4zfkzeA==")
//                .roles("CUSTOMER");
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return CustomEncoderFactories.createDelegatingPasswordEncoder();
    }

}
