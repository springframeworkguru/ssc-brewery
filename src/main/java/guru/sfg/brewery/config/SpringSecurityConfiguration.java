package guru.sfg.brewery.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(adminUserName)
                .password("{noop}" + adminPassword)
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{noop}password")
                .roles("USER");
    }

}
