package guru.sfg.brewery.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.DUNEDIN_USER;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.KEYWEST_USER;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.STPETE_USER;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.ST_PETE_DISTRIBUTING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerIT extends AbstractBaseIT {

    public static final String API_ROOT = "/api/v1/customers/";

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ObjectMapper objectMapper;

    private Customer stPeteCustomer;
    Collection<Beer> loadedBeers;

    @BeforeEach
    void setupCustomers() {
        stPeteCustomer = customerRepository.findAllByCustomerName(ST_PETE_DISTRIBUTING).orElseThrow();

        loadedBeers = beerRepository.findAll();
    }

    @Test
    void createOrderWithNoAuth() throws Exception {
        BeerOrderDto dto = buildOrderDto(stPeteCustomer, loadedBeers.iterator().next().getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void createOrderWithAdminUser() throws Exception {
        BeerOrderDto dto = buildOrderDto(stPeteCustomer, loadedBeers.iterator().next().getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(STPETE_USER)
    @Test
    void createOrderWithAuthorizedUser() throws Exception {
        BeerOrderDto dto = buildOrderDto(stPeteCustomer, loadedBeers.iterator().next().getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(KEYWEST_USER)
    @Test
    void createOrderWithNotAuthorizedUser() throws Exception {
        BeerOrderDto dto = buildOrderDto(stPeteCustomer, loadedBeers.iterator().next().getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersWithNoAuthorization() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void listOrdersWithAdminAuthorization() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(STPETE_USER)
    @Test
    void listOrdersWithAuthorizedUser() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(DUNEDIN_USER)
    @Test
    void listOrdersWithNotAuthorizationUser() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    void getOrderIdNoAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("spring")
    @Test
    void getOrderIdWithAdminUser() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(STPETE_USER)
    @Test
    void getOrdersIdWithOwningUser() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(KEYWEST_USER)
    @Test
    void getOrderIdWithNotOwningUser() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    void pickUpOrderNoAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Transactional
    @Test
    void pickUpOrderAdminUser() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isNoContent());
    }

    @WithUserDetails(STPETE_USER)
    @Transactional
    @Test
    void pickUpOrderCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isNoContent());
    }

    @WithUserDetails(KEYWEST_USER)
    @Transactional
    @Test
    void pickUpOrderCustomerWithDifferentCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isForbidden());
    }

    private @NotNull BeerOrderDto buildOrderDto(@NotNull Customer customer, @NotNull UUID id) {
        BeerOrderLineDto beerOrderLineDto = BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(id)
                .orderQuantity(5)
                .build();
        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(Collections.singletonList(beerOrderLineDto))
                .build();
    }

}
