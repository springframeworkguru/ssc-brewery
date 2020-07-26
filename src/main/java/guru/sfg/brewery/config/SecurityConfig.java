package guru.sfg.brewery.config;

import guru.sfg.brewery.filter.RestHeaderAuthFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager){
        RestHeaderAuthFilter restHeaderAuthFilter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        restHeaderAuthFilter.setAuthenticationManager(authenticationManager);
        return restHeaderAuthFilter;
    }
    // By default, Spring Security turns on csrf protection.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
        .csrf().disable();
        http
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/h2-console/**").permitAll() // do not use in production
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers("/beers/find", "/beers*").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();
                } )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();

        // h2 console config
        http.headers().frameOptions().sameOrigin();
    }

    // Configuring In Memory Authentication using Authentication Fluent API.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("guru")
                .roles()
                .and()
                .withUser("user")
                .password("{noop}password")
                .roles("USER");

    }

    // This is kinda an old way of creating InMemory UserDetails Service. Alternatively, we can use
    // In Memory Authentication Fluent API. See above
//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("guru")
//                .roles("ADMIN")
//                .build();
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        // InMemoryUserDetailsManager implements UserDetailsService thus overriding default UserDetailsService
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
