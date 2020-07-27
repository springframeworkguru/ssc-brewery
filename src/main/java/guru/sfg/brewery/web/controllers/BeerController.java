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
import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/beers")
@Controller
public class BeerController {

    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;

    @PreAuthorize("permitAll()")
    @RequestMapping("/find")
    public @NotNull String findBeers(@NotNull Model model) {
        model.addAttribute("beer", Beer.builder().build());
        return "beers/findBeers";
    }

    @PreAuthorize("permitAll()")
    @GetMapping
    public @NotNull String processFindFormReturnMany(
            @NotNull Beer beer,
            @NotNull BindingResult result,
            @NotNull Model model) {
        // find beers by name
        //ToDO: Add Service
        //ToDO: Get paging data from view
        Page<Beer> pagedResult = beerRepository.findAllByBeerNameIsLike("%" + beer.getBeerName() + "%", createPageRequest(0, 10, Sort.Direction.DESC, "beerName"));
        List<Beer> beerList = pagedResult.getContent();
        if (beerList.isEmpty()) {
            // no beers found
            result.rejectValue("beerName", "notFound", "not found");
            return "beers/findBeers";
        } else if (beerList.size() == 1) {
            // 1 beer found
            beer = beerList.get(0);
            return "redirect:/beers/" + beer.getId();
        } else {
            // multiple beers found
            model.addAttribute("selections", beerList);
            return "beers/beerList";
        }
    }


    @PreAuthorize("hasAuthority('beer.read')")
    @GetMapping("/{beerId}")
    public @NotNull ModelAndView showBeer(@NotNull @PathVariable UUID beerId) {
        ModelAndView mav = new ModelAndView("beers/beerDetails");
        //ToDO: Add Service
        mav.addObject(beerRepository.findById(beerId).get());
        return mav;
    }

    @PreAuthorize("hasAuthority('beer.create')")
    @GetMapping("/new")
    public @NotNull String initCreationForm(@NotNull Model model) {
        model.addAttribute("beer", Beer.builder().build());
        return "beers/createBeer";
    }

    @PreAuthorize("hasAuthority('beer.create')")
    @PostMapping("/new")
    public @NotNull String processCreationForm(@NotNull Beer beer) {
        //ToDO: Add Service
        Beer newBeer = Beer.builder()
                .beerName(beer.getBeerName())
                .beerStyle(beer.getBeerStyle())
                .minOnHand(beer.getMinOnHand())
                .price(beer.getPrice())
                .quantityToBrew(beer.getQuantityToBrew())
                .upc(beer.getUpc())
                .build();

        Beer savedBeer = beerRepository.save(newBeer);
        return "redirect:/beers/" + savedBeer.getId();
    }

    @PreAuthorize("hasAuthority('beer.update')")
    @GetMapping("/{beerId}/edit")
    public @NotNull String initUpdateBeerForm(
            @NotNull @PathVariable UUID beerId,
            @NotNull Model model) {
        if (beerRepository.findById(beerId).isPresent())
            model.addAttribute("beer", beerRepository.findById(beerId).get());
        return "beers/createOrUpdateBeer";
    }

    @PreAuthorize("hasAuthority('beer.update')")
    @PostMapping("/{beerId}/edit")
    public @NotNull String processUpdateForm(@NotNull @Valid Beer beer, @NotNull BindingResult result) {
        if (result.hasErrors()) {
            return "beers/createOrUpdateBeer";
        } else {
            //ToDO: Add Service
            Beer savedBeer = beerRepository.save(beer);
            return "redirect:/beers/" + savedBeer.getId();
        }
    }

    private @NotNull PageRequest createPageRequest(
            int page,
            int size,
            @NotNull Sort.Direction sortDirection,
            @NotNull String propertyName) {
        return PageRequest.of(page,
                size,
                Sort.by(sortDirection, propertyName));
    }

}


