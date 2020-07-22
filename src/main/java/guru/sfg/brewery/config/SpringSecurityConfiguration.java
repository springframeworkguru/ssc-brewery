package guru.sfg.brewery.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

}
