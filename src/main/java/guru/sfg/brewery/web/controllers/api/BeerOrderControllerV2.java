package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.security.perms.PreAuthorizeBeerOrderReadV2;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/orders/")
public class BeerOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    @PreAuthorizeBeerOrderReadV2
    @GetMapping
    public @Nullable BeerOrderPagedList listOrders(
            @NotNull @AuthenticationPrincipal User user,
            @Nullable @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @Nullable @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (user.getCustomer() == null) {
            return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }

        return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
    }

    @PreAuthorizeBeerOrderReadV2
    @GetMapping("{orderId}")
    public @NotNull BeerOrderDto getOrder(@NotNull @PathVariable("orderId") UUID orderId) {
        BeerOrderDto beerOrderDto = beerOrderService.getOrderById(orderId);
        if (beerOrderDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found");
        }

        log.debug("Found Order: " + beerOrderDto);

        return beerOrderDto;
    }

}
