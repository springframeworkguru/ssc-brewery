package guru.sfg.brewery.config;

import guru.sfg.brewery.security.BreweryPasswordEncoderFactories;
import guru.sfg.brewery.security.RestHeaderAuthenticationFilter;
import guru.sfg.brewery.security.RestUriParameterAuthenticationFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
        return BreweryPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private @NotNull RestHeaderAuthenticationFilter restHeaderAuthenticationFilter(
            @NotNull AuthenticationManager manager) {
        RestHeaderAuthenticationFilter filter =
                new RestHeaderAuthenticationFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(manager);
        return filter;
    }

    private @NotNull RestUriParameterAuthenticationFilter restPathAttributeAuthenticationFilter(
            @NotNull AuthenticationManager manager) {
        RestUriParameterAuthenticationFilter filter =
                new RestUriParameterAuthenticationFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(manager);
        return filter;
    }

    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {
        http

                // TODO: figure out why this blocks logging into H@ console?
                // .addFilterBefore(restHeaderAuthenticationFilter(authenticationManager()),
                // UsernamePasswordAuthenticationFilter.class)
                // .addFilterBefore(restPathAttributeAuthenticationFilter(authenticationManager()),
                //         UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()

                .authorizeRequests(authorize ->
                        authorize.antMatchers("/h2-console/**").permitAll() //do not use in production!
                                .antMatchers(PUBLIC_ANT_URLS).permitAll()
                                .antMatchers(HttpMethod.GET, PUBLIC_ANT_GET_URLS).permitAll()
                                .mvcMatchers(HttpMethod.GET, PUBLIC_MVC_GET_URLS).permitAll())
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and()
                .headers().frameOptions().sameOrigin();
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
                .password("{bcrypt15}$2a$15$KFcrLSEK0eLOXCro8vi2P.HWlTwl.VJgQHmFsQd8qbRfTsdmf6goC")
                .roles("CUSTOMER");
    }

}
