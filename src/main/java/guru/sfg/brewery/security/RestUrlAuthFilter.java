package guru.sfg.brewery.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class RestUrlAuthFilter extends AbstractRestAuthFilter {
    public RestUrlAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    protected String getPassword(HttpServletRequest request) {
        return request.getParameter("Api-secret");
    }

    @Override
    protected String getUserName(HttpServletRequest request) {
        return request.getParameter("Api-key");
    }


}
