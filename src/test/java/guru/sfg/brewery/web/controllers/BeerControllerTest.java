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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

@ExtendWith(MockitoExtension.class)
class BeerControllerTest {
    @Mock
    BeerRepository beerRepository;

    @InjectMocks
    BeerController controller;
    List<Beer> beerList;
    UUID uuid;
    Beer beer;

    MockMvc mockMvc;
    Page<Beer> beers;
    Page<Beer> pagedResponse;

    @BeforeEach
    void setUp() {
        beerList = new ArrayList<Beer>();
        beerList.add(Beer.builder().build());
        beerList.add(Beer.builder().build());
        pagedResponse = new PageImpl(beerList);

        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        uuid = UUID.fromString(id);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void findBeers() throws Exception{
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
        verifyZeroInteractions(beerRepository);
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
    void showBeer() throws Exception{

        when(beerRepository.findById(uuid)).thenReturn(Optional.of(Beer.builder().id(uuid).build()));
        mockMvc.perform(get("/beers/"+uuid))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/beerDetails"))
                .andExpect(model().attribute("beer", hasProperty("id", is(uuid))));
    }

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/beers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
        verifyZeroInteractions(beerRepository);
    }

    @Test
    void processCreationForm() throws Exception {
        when(beerRepository.save(ArgumentMatchers.any())).thenReturn(Beer.builder().id(uuid).build());
        mockMvc.perform(post("/beers/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/beers/"+ uuid))
                .andExpect(model().attributeExists("beer"));
        verify(beerRepository).save(ArgumentMatchers.any());
    }

    @Test
    void initUpdateBeerForm() throws Exception{
        when(beerRepository.findById(uuid)).thenReturn(Optional.of(Beer.builder().id(uuid).build()));
        mockMvc.perform(get("/beers/"+uuid+"/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createOrUpdateBeer"))
                .andExpect(model().attributeExists("beer"));
        verifyZeroInteractions(beerRepository);
    }

    @Test
    void processUpdationForm() throws Exception {
        when(beerRepository.save(ArgumentMatchers.any())).thenReturn(Beer.builder().id(uuid).build());

        mockMvc.perform(post("/beers/"+uuid+"/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/beers/"+uuid))
                .andExpect(model().attributeExists("beer"));

        verify(beerRepository).save(ArgumentMatchers.any());
    }
}