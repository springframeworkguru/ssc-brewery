package guru.sfg.brewery.config;

import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  PasswordEncoder passwordEncoder() {
    return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests(authorize ->
            authorize
                .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                .antMatchers("/beers*", "/beers/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/beers/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll()
        )
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .and()
        .httpBasic();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .inMemoryAuthentication()
        .withUser("spring")
        .password("{bcrypt}$2a$10$DQ84ZEd204ZKyiwJQMU3POi4kxR6MGCnEs6xdLML2mb3bc2rGLY/e")
        .roles("ADMIN")
        .and()
        .withUser("user")
        .password(
            "{sha256}fa8ae9d007c1db40dd4be3ffccd2e914f9afb5a21617a6037f5d4a6" +
                "7e07b3912b6812b3c97960f8d")
        .roles("USER");
    auth
        .inMemoryAuthentication()
        .withUser("scott")
        .password("{bcrypt15}$2a$15$9d8UB7VEmMi/tg8U6tH2vuUvLwF8n2CrTU5sMJ57hDXvYjuKxql.K")
        .roles("CUSTOMER");
  }


}