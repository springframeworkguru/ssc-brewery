package guru.sfg.brewery.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by jt on 7/7/20.
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.read') OR " +
        "hasAuthority('customer.order.read') " +
        " AND @beerOrderAuthenticationManger.customerIdMatches(authentication, #customerId )")
public @interface BeerOrderReadPermission {
}
