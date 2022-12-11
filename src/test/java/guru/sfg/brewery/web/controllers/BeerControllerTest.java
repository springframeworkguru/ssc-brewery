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

package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {
    @MockBean
    BeerRepository beerRepository;

    @Autowired
    MockMvc mockMvc;

    List<Beer> beerList;
    UUID uuid;
    Beer beer;

    Page<Beer> beers;
    Page<Beer> pagedResponse;

    @BeforeEach
    void setUp() {
        beerList = new ArrayList<>();
        beerList.add(Beer.builder().build());
        beerList.add(Beer.builder().build());
        pagedResponse = new PageImpl<>(beerList);

        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        uuid = UUID.fromString(id);

    }

    @Test
    void findBeers() throws Exception{
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
        verifyNoMoreInteractions(beerRepository);
    }

    //ToDO: Mocking Page
     void processFindFormReturnMany() throws Exception{
        when(beerRepository.findAllByBeerName(anyString(), PageRequest.of(0,
              10,Sort.Direction.DESC,"beerName"))).thenReturn(pagedResponse);
        mockMvc.perform(get("/beers"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/beerList"))
                .andExpect(model().attribute("selections", hasSize(2)));
    }


    @Test
    @WithMockUser(username = "MockUser",password = "MockPWD")
    void showBeer() throws Exception{

        when(beerRepository.findById(uuid)).thenReturn(Optional.of(Beer.builder().id(uuid).build()));
        mockMvc.perform(get("/beers/"+uuid))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/beerDetails"))
                .andExpect(model().attribute("beer", hasProperty("id", is(uuid))));
    }

    @Test
    @WithMockUser(username = "MockUser",password = "MockPWD")
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/beers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
        verifyNoMoreInteractions(beerRepository);
    }

    @Test
    @WithMockUser(username = "MockUser",password = "MockPWD")
    void processCreationForm() throws Exception {
        when(beerRepository.save(ArgumentMatchers.any())).thenReturn(Beer.builder().id(uuid).build());
        mockMvc.perform(post("/beers/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/beers/"+ uuid));
        verify(beerRepository).save(ArgumentMatchers.any());
    }

    @Test
    @WithMockUser(username = "MockUser",password = "MockPWD")
    void initUpdateBeerForm() throws Exception{
        when(beerRepository.findById(uuid)).thenReturn(Optional.of(Beer.builder().id(uuid).build()));
        mockMvc.perform(get("/beers/"+uuid+"/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createOrUpdateBeer"))
                .andExpect(model().attributeExists("beer"));
        verify(beerRepository,atMost(2)).findById(any());
    }

    @Test
    @WithMockUser(username = "MockUser",password = "MockPWD")
    void processUpdationForm() throws Exception {
        when(beerRepository.save(ArgumentMatchers.any())).thenReturn(Beer.builder().id(uuid).build());

        mockMvc.perform(post("/beers/"+uuid+"/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/beers/"+uuid));
        verify(beerRepository).save(ArgumentMatchers.any());
    }
}