package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorize -> {
            authorize.antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                    .antMatchers("/beers/find", "/beers*").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/beer/**", "/api/v1/beerUpc/**").permitAll();
        })
                .authorizeRequests()
                .anyRequest()
                .authenticated().and()
                .formLogin().and()
                .httpBasic();
    }

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("password")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        UserDetails customer = User.withDefaultPasswordEncoder()
//                .username("scott")
//                .password("tiger")
//                .roles("CUSTOMER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user, customer);
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("password")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("password")
                .roles("USER");

        auth.inMemoryAuthentication().withUser("scott").password("tiger").roles("CUSTOMER");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
