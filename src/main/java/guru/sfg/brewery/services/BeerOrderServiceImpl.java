/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package guru.sfg.brewery.services;

import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.domain.OrderStatusEnum;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.mappers.BeerOrderMapper;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public @Nullable BeerOrderPagedList listOrders(@NotNull UUID customerId, @NotNull Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<BeerOrder> beerOrderPage =
                    beerOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new BeerOrderPagedList(beerOrderPage
                    .stream()
                    .map(beerOrderMapper::beerOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                    beerOrderPage.getPageable().getPageNumber(),
                    beerOrderPage.getPageable().getPageSize()),
                    beerOrderPage.getTotalElements());
        }
        return null;
    }

    @Transactional
    @Override
    public @Nullable BeerOrderDto placeOrder(@NotNull UUID customerId, @Nullable BeerOrderDto beerOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null); //should not be set by outside client
            beerOrder.setCustomer(customerOptional.get());
            beerOrder.setOrderStatus(OrderStatusEnum.NEW);

            beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));

            BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);

            log.debug("Saved Beer Order: " + beerOrder.getId());

            return beerOrderMapper.beerOrderToDto(savedBeerOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public @NotNull BeerOrderDto getOrderById(@Nullable UUID customerId, @NotNull UUID orderId) {
        return Optional.of(getOrder(customerId, orderId))
                .map(beerOrderMapper::beerOrderToDto)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public void pickupOrder(@NotNull UUID customerId, @NotNull UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);

        beerOrderRepository.save(beerOrder);
    }

    private @NotNull BeerOrder getOrder(@Nullable UUID customerId, @NotNull UUID orderId) {
        if (customerId == null) {
            throw new RuntimeException("Customer Cannot Be Found");
        }

        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isEmpty()) {
            throw new RuntimeException("Customer Not Found");
        }
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);

        if (beerOrderOptional.isPresent()) {
            BeerOrder beerOrder = beerOrderOptional.get();

            // fall to exception if customer id's do not match - order not for customer
            if (beerOrder.getCustomer().getId().equals(customerId)) {
                return beerOrder;
            }
        }
        throw new RuntimeException("Beer Order Not Found");
    }

}
