package guru.sfg.brewery.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String[] PUBLIC_ANT_URLS =
            {"/", "/login", "/beers/find", "/beers*", "/webjars/**", "/resources/**"};
    public static final String[] PUBLIC_ANT_GET_URLS = {"/api/v1/beer/**"};
    public static final String[] PUBLIC_MVC_GET_URLS = {"/api/v1/beerUpc/{upc}"};

    @Value("${spring.security.user.name:}")
    String adminUserName;
    @Value("${spring.security.user.password:}")
    String adminPassword;

    @Bean
    public @NotNull PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize ->
                        authorize.antMatchers(PUBLIC_ANT_URLS).permitAll()
                                .antMatchers(HttpMethod.GET, PUBLIC_ANT_GET_URLS).permitAll()
                                .mvcMatchers(HttpMethod.GET, PUBLIC_MVC_GET_URLS).permitAll())
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    @Override
    protected void configure(@NotNull AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(adminUserName)
                .password("{noop}" + adminPassword)
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{bcrypt}$2a$13$ITY1oZe1aqaLcf6mzB3a.ujQTEuS0dKs7ZXyF3NJn/vcb48oOx32m")
                .roles("USER")
                .and()
                .withUser("scott")
                .password("{ldap}{SSHA}o7Tlxi8m1Pxtz4/Sw2I4qfJKL0RNXShvQL6JVw==")
                .roles("CUSTOMER");
    }

}
