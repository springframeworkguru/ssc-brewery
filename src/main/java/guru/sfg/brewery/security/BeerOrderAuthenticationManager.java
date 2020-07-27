package guru.sfg.brewery.security;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class BeerOrderAuthenticationManager {

    public boolean customerIdMatches(@NotNull Authentication authentication, @Nullable UUID customerId) {
        User authenticatedUser = (User) authentication.getPrincipal();
        log.debug("Auth User Customer Id [" + authenticatedUser.getCustomer().getId() +
                "] and Customer Id [" + customerId + "]");
        return authenticatedUser.getCustomer().getId().equals(customerId);
    }

}
