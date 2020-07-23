package guru.sfg.brewery.security;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public abstract class AbstractRestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public AbstractRestAuthenticationFilter(@NotNull RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public void doFilter(
            @NotNull ServletRequest req,
            @NotNull ServletResponse res,
            @NotNull FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (log.isDebugEnabled()) {
            log.debug("Processing very legacy authentication request.");
        }

        try {
            Authentication authResult = attemptAuthentication(request, response);

            if (authResult == null) {
                chain.doFilter(request, response);
            } else {
                successfulAuthentication(request, response, chain, authResult);
            }
        } catch (AuthenticationException exception) {
            unsuccessfulAuthentication(request, response, exception);
        }
    }

    @Override
    public @Nullable Authentication attemptAuthentication(
            @NotNull HttpServletRequest request,
            @Nullable HttpServletResponse response)
            throws AuthenticationException {
        String username = getUsername(request);
        String password = getPassword(request);

        if (log.isDebugEnabled()) {
            log.debug("Authenticating:" + username);
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        if (StringUtils.isEmpty(username)) {
            return null;
        }

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable FilterChain chain,
            @NotNull Authentication authResult) {
        log.debug("Authentication success. Updating SecurityContextHolder.");
        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(
            @Nullable HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull AuthenticationException failed) throws IOException {
        SecurityContextHolder.clearContext();
        if (log.isDebugEnabled()) {
            log.debug("Authentication request failed: " + failed.toString(), failed);
            log.debug("Updated SecurityContextHolder to contain null Authentication");
        }

        response.sendError(HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    protected abstract @NotNull String getUsername(@NotNull HttpServletRequest request);

    protected abstract @NotNull String getPassword(@NotNull HttpServletRequest request);

}
