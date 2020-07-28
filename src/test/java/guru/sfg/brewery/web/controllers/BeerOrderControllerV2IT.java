package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.DUNEDIN_USER;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.STPETE_USER;
import static guru.sfg.brewery.bootstrap.DefaultBreweryLoader.ST_PETE_DISTRIBUTING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerV2IT extends AbstractBaseIT {

    public static final String API_ROOT = "/api/v2/orders/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    Customer stPeteCustomer;
    List<Beer> loadedBeers;

    @BeforeEach
    void setupBeerOrders() {
        stPeteCustomer = customerRepository.findAllByCustomerName(ST_PETE_DISTRIBUTING).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    @Test
    void listOrdersWithoutAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @WithUserDetails(STPETE_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @WithUserDetails(DUNEDIN_USER)
    @Test
    void listOrdersCustomerDunedinAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("spring")
    @Test
    void getByOrderIdADMIN() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(STPETE_USER)
    @Test
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DUNEDIN_USER)
    @Test
    void getByOrderIdCustomerNOTAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();

        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().isNotFound());
    }

}
