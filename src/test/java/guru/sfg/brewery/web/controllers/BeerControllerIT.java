package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;

/*
 * WebMvcTest behövs bl.a. för att kunna använda WebApplicationContext vilket ger åtkomst
 * till spring security filters.
 */

@WebMvcTest
public class BeerControllerIT {

	@Autowired
	WebApplicationContext wac;
	
	
	MockMvc mockMvc;
	
	
	/*
	 * MockBean tells mockito to create a mock of spring-beans in the spring-context
	 * and injects it in. 
	 * 
	 * 
	 */
	
	
	@MockBean
	BeerRepository beerRepository;
	
	@MockBean
	BeerInventoryRepository beerInventoryRepository;
	
	@MockBean
	BreweryService breweryService;
	
	@MockBean
	CustomerRepository customerRepository;
	
	@MockBean
	BeerService beerService;
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.apply(springSecurity())
				.build();
	}
	
	
	/* 
	 * Use this to test the logic
	 * ---------------------------
	 * Talar om att en användare (mockad) med användarnamnet mockUser har loggat in. 
	 * Användaren behöver inte vara en riktig användare och kan heta vadsomhelst.
	 */
	@WithMockUser("mockUser")		
	@Test
	void findBeers() throws Exception {
		mockMvc.perform(get("/beers/find"))
			.andExpect(status().isOk())
			.andExpect(view().name("beers/findBeers"))
			.andExpect(model().attributeExists("beer"));
	}
	
	
	/*
	 * Use this to test logic & function
	 * ---------------------------------
	 * 
	 * Använd först en user/password kombo som inte finns foo och bar för att bekrafta fail.
	 * Prova med användare som finns o bekräfta passing-test.
	 * 
	 * Fungerar inte med mockMvc.
	 * 	
	 *  Oavsett om användaren finns på riktigt eller inte.
	 * 
	 */
	@Test
	void findBeersWithHttpBasic() throws Exception {
		
		mockMvc.perform(get("/beers/find").with(httpBasic("user", "passwd")))
			.andExpect(status().isOk())
			.andExpect(view().name("beers/findBeers"))
			.andExpect(model().attributeExists("beer"));
	}
	
}

