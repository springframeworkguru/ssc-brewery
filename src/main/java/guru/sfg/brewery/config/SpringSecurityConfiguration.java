package guru.sfg.brewery.config;

import guru.sfg.brewery.security.BreweryPasswordEncoderFactories;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String[] PUBLIC_ANT_URLS =
            {"/", "/login", "/beers/find", "/webjars/**", "/resources/**"};
    public static final String[] SECURED_BEER_URLS = {"/api/v1/beer/**", "/beers*"};
    public static final String[] SECURED_BEER_UPC_URLS = {"/api/v1/beerUpc/{upc}"};

    public static final String[] SECURED_BREWERIES_URLS = {"/brewery/api/v1/breweries", "/brewery/breweries"};

    @Bean
    public @NotNull PasswordEncoder passwordEncoder() {
        return BreweryPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests(authorize ->
                        authorize.antMatchers("/h2-console/**").permitAll() //do not use in production!
                                .antMatchers(PUBLIC_ANT_URLS).permitAll()
                                .antMatchers(HttpMethod.GET, SECURED_BEER_URLS).hasAnyRole("ADMIN", "USER", "CUSTOMER")
                                .mvcMatchers(HttpMethod.GET, SECURED_BREWERIES_URLS).hasAnyRole("ADMIN", "CUSTOMER")
                                .mvcMatchers(HttpMethod.GET, SECURED_BEER_UPC_URLS).hasAnyRole("ADMIN", "USER", "CUSTOMER"))
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and()
                .headers().frameOptions().sameOrigin();
    }

}
