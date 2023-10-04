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

package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.security.perms.BeerCreatePermission;
import guru.sfg.brewery.security.perms.BeerDeletePermission;
import guru.sfg.brewery.security.perms.BeerReadPermission;
import guru.sfg.brewery.security.perms.BeerUpdatePermission;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.web.model.BeerDto;
import guru.sfg.brewery.web.model.BeerPagedList;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@RestController
public class BeerRestController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerService beerService;

    @BeerReadPermission
    @GetMapping(produces = { "application/json" }, path = "beer")
    public ResponseEntity<BeerPagedList> listBeers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                   @RequestParam(value = "beerName", required = false) String beerName,
                                                   @RequestParam(value = "beerStyle", required = false) BeerStyleEnum beerStyle,
                                                   @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        log.debug("Listing Beers");

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        BeerPagedList beerList = beerService.listBeers(beerName, beerStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);

        return new ResponseEntity<>(beerList, HttpStatus.OK);
    }

    @BeerReadPermission
    @GetMapping(path = {"beer/{beerId}"}, produces = { "application/json" })
    public ResponseEntity<BeerDto> getBeerById(@PathVariable("beerId") UUID beerId,
                                               @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        log.debug("Get Request for BeerId: " + beerId);

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        return new ResponseEntity<>(beerService.findBeerById(beerId, showInventoryOnHand), HttpStatus.OK);
    }

    @BeerReadPermission
    @GetMapping(path = {"beerUpc/{upc}"}, produces = { "application/json" })
    public ResponseEntity<BeerDto> getBeerByUpc(@PathVariable("upc") String upc){
        return new ResponseEntity<>(beerService.findBeerByUpc(upc), HttpStatus.OK);
    }

    @BeerCreatePermission
    @PostMapping(path = "beer")
    public ResponseEntity saveNewBeer(@Valid @RequestBody BeerDto beerDto){

        BeerDto savedDto = beerService.saveBeer(beerDto);

        HttpHeaders httpHeaders = new HttpHeaders();

        //todo hostname for uri
        httpHeaders.add("Location", "/api/v1/beer_service/" + savedDto.getId().toString());

        return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
    }

    @BeerUpdatePermission
    @PutMapping(path = {"beer/{beerId}"}, produces = { "application/json" })
    public ResponseEntity updateBeer(@PathVariable("beerId") UUID beerId, @Valid @RequestBody BeerDto beerDto){

        beerService.updateBeer(beerId, beerDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @BeerDeletePermission
    @DeleteMapping({"beer/{beerId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBeer(@PathVariable("beerId") UUID beerId){
        beerService.deleteById(beerId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<List> badReqeustHandler(ConstraintViolationException e){
        List<String> errors = new ArrayList<>(e.getConstraintViolations().size());

        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getPropertyPath().toString() + " : " + constraintViolation.getMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
