package guru.sfg.brewery.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
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

/**
 * AbstractAuthenticationProcessingFilter - abstract processor of browser-based HTTP-based authentication requests.
 * The filter requires that you set the authenticationManager property. An authenticationManager is required to process
 * the authentication request tokens creating by implementing classes. This filter will intercept a request and attempt
 * to perform authentication from that request if the request URL matches the value of the filterProcessesUrl property.
 * This behaviour can be modified by overriding the method requiresAuthentication. Authentication is performed by the
 * attemptAuthentication method, which must be implemented by subclasses.
 *
 * If authentication is successful, the resulting Authentication object will be placed into the SecurityContext
 * for the current thread, which is guaranteed to have already been created by an earlier filter.
 * */
@Slf4j
public class RestHeaderAuthFilter extends AbstractAuthenticationProcessingFilter {

    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {

        String userName = getUsername(httpServletRequest);
        String password = getPassword(httpServletRequest);

        if (userName == null){
            userName = "";
        }
        if (password == null){
            password = "";
        }
        log.debug("Authenticating user: {}", userName);

        // An Authentication implementation that is designed for simple presentation of a username and a password.
        // Needed for work with authentication manager.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        // AuthenticationManager is configured as InMemory in fluent api setting
        if (!StringUtils.isEmpty(userName)){
            return this.getAuthenticationManager().authenticate(token);
        }
        return null;
    }

    private String getPassword(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("Api-Secret");
    }

    private String getUsername(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("Api-Key");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        if (!this.requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Request is to process authentication");
            }
            Authentication authResult = attemptAuthentication(request, response);
            try {
                if (authResult != null){
                    this.successfulAuthentication(request, response, chain, authResult);
                }else {
                    chain.doFilter(request, response);
                }
            } catch (AuthenticationException e){
                log.error("Authentication Failed");
                unsuccessfulAuthentication(request, response, e);
            }
        }
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }
        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        if (this.log.isDebugEnabled()) {
            log.debug("Authentication request failed: " + failed.toString(), failed);
            log.debug("Updated SecurityContextHolder to contain null Authentication");
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }
}
