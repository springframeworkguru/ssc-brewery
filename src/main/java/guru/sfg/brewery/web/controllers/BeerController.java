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
import guru.sfg.brewery.security.perms.BeerCreatePermission;
import guru.sfg.brewery.security.perms.BeerReadPermission;
import guru.sfg.brewery.security.perms.BeerUpdatePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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


    @BeerReadPermission
    @RequestMapping("/find")
    public String findBeers(Model model) {
        model.addAttribute("beer", Beer.builder().build());
        return "beers/findBeers";
    }

    @BeerReadPermission
    @GetMapping
    public String processFindFormReturnMany(Beer beer, BindingResult result, Model model) {
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


    @BeerReadPermission
    @GetMapping("/{beerId}")
    public ModelAndView showBeer(@PathVariable UUID beerId) {
        ModelAndView mav = new ModelAndView("beers/beerDetails");
        //ToDO: Add Service
        mav.addObject(beerRepository.findById(beerId).get());
        return mav;
    }

    @BeerCreatePermission
    @GetMapping("/new")
    public String initCreationForm(Model model) {
        model.addAttribute("beer", Beer.builder().build());
        return "beers/createBeer";
    }

    @BeerCreatePermission
    @PostMapping("/new")
    public String processCreationForm(Beer beer) {
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

    @BeerUpdatePermission
    @GetMapping("/{beerId}/edit")
    public String initUpdateBeerForm(@PathVariable UUID beerId, Model model) {
        if (beerRepository.findById(beerId).isPresent())
            model.addAttribute("beer", beerRepository.findById(beerId).get());
        return "beers/createOrUpdateBeer";
    }

    @BeerUpdatePermission
    @PostMapping("/{beerId}/edit")
    public String processUpdateForm(@Valid Beer beer, BindingResult result) {
        if (result.hasErrors()) {
            return "beers/createOrUpdateBeer";
        } else {
            //ToDO: Add Service
            Beer savedBeer = beerRepository.save(beer);
            return "redirect:/beers/" + savedBeer.getId();
        }
    }

    private PageRequest createPageRequest(int page, int size, Sort.Direction sortDirection, String propertyName) {
        return PageRequest.of(page,
                size,
                Sort.by(sortDirection, propertyName));
    }
}


