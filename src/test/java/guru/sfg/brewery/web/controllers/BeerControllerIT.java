package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/*
 * WebMvcTest behövs bl.a. för att kunna använda WebApplicationContext vilket ger åtkomst
 * till spring security filters.
 */

@WebMvcTest
public class BeerControllerIT extends BaseIT{


	
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

