package guru.sfg.brewery.config;

import guru.sfg.brewery.security.BreweryPasswordEncoderFactories;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String[] PUBLIC_URLS =
            {"/", "/login", "/beers/find", "/webjars/**", "/resources/**"};

    /**
     * This bean is needed for SPeL.
     *
     * @return security evaluation context extension
     */
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Bean
    public @NotNull PasswordEncoder passwordEncoder() {
        return BreweryPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/h2-console/**", "/api/**")
                .and()
                .authorizeRequests(authorize ->
                        authorize.mvcMatchers(PUBLIC_URLS).permitAll()
                                .mvcMatchers("/h2-console/**").permitAll()) //do not use in production!
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
