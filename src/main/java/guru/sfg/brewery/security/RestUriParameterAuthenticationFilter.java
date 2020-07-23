package guru.sfg.brewery.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class RestUriParameterAuthenticationFilter extends AbstractRestAuthenticationFilter {

    public RestUriParameterAuthenticationFilter(@NotNull RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    protected @NotNull String getUsername(@NotNull HttpServletRequest request) {
        String username = request.getParameter("user");
        return username == null ? "" : username;
    }

    @Override
    protected @NotNull String getPassword(@NotNull HttpServletRequest request) {
        String password = request.getParameter("password");
        return password == null ? "" : password;
    }

}
