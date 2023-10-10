package guru.sfg.brewery.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by jt on 7/7/20.
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.pickup') OR " +
        "hasAuthority('customer.order.pickup') " +
        " AND @beerOrderAuthenticationManger.customerIdMatches(authentication, #customerId )")
public @interface BeerOrderPickupPermission {
}
