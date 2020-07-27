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


import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BeerOrderService {

    @Nullable BeerOrderPagedList listOrders(@NotNull UUID customerId, @NotNull Pageable pageable);

    @Nullable BeerOrderDto placeOrder(@NotNull UUID customerId, @Nullable BeerOrderDto beerOrderDto);

    @NotNull BeerOrderDto getOrderById(@Nullable UUID customerId, @NotNull UUID orderId);

    void pickupOrder(@NotNull UUID customerId, @NotNull UUID orderId);

}
