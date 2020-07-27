package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.security.perms.PreAuthorizeBeerOrderCreate;
import guru.sfg.brewery.security.perms.PreAuthorizeBeerOrderRead;
import guru.sfg.brewery.security.perms.PreAuthorizeOrderPickup;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("api/v1/customers/{customerId}/")
@RestController
@RequiredArgsConstructor
public class BeerOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    @PreAuthorizeBeerOrderRead
    @GetMapping("orders")
    public @Nullable BeerOrderPagedList listOrders(
            @NotNull @PathVariable("customerId") UUID customerId,
            @Nullable @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @Nullable @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PreAuthorizeBeerOrderCreate
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("orders")
    public BeerOrderDto placeOrder(
            @NotNull @PathVariable("customerId") UUID customerId,
            @Nullable @RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @PreAuthorizeBeerOrderRead
    @GetMapping("orders/{orderId}")
    public @NotNull BeerOrderDto getOrders(
            @Nullable @PathVariable("customerId") UUID customerId,
            @NotNull @PathVariable("orderId") UUID orderId) {
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @PreAuthorizeOrderPickup
    @PutMapping("orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(
            @NotNull @PathVariable("customerId") UUID customerId,
            @NotNull @PathVariable("orderId") UUID orderId) {
        beerOrderService.pickupOrder(customerId, orderId);
    }

}
