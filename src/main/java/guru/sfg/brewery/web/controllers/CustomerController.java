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

import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping("/customers")
@Controller
public class CustomerController {

    //ToDO: Add service
    private final CustomerRepository customerRepository;

    @RequestMapping("/find")
    public String findCustomers(Model model){
        model.addAttribute("customer", Customer.builder().build());
        return "customers/findCustomers";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping
    public String processFindFormReturnMany(Customer customer, BindingResult result, Model model){
        // find customers by name
        //ToDO: Add Service
        List<Customer> customers = customerRepository.findAllByCustomerNameLike("%" + customer.getCustomerName() + "%");
        if (customers.isEmpty()) {
            // no customers found
            result.rejectValue("customerName", "notFound", "not found");
            return "customers/findCustomers";
        } else if (customers.size() == 1) {
            // 1 customer found
            customer = customers.get(0);
            return "redirect:/customers/" + customer.getId();
        } else {
            // multiple customers found
            model.addAttribute("selections", customers);
            return "customers/customerList";
        }
    }

   @GetMapping("/{customerId}")
    public ModelAndView showCustomer(@PathVariable UUID customerId) {
        ModelAndView mav = new ModelAndView("customers/customerDetails");
        //ToDO: Add Service
        mav.addObject(customerRepository.findById(customerId).get());
        return mav;
    }

    @GetMapping("/new")
    public String initCreationForm(Model model) {
        model.addAttribute("customer", Customer.builder().build());
        return "customers/createCustomer";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public String processCreationForm(Customer customer) {
        //ToDO: Add Service
        Customer newCustomer = Customer.builder()
                .customerName(customer.getCustomerName())
                .build();

        Customer savedCustomer= customerRepository.save(newCustomer);
        return "redirect:/customers/" + savedCustomer.getId();
    }

    @GetMapping("/{customerId}/edit")
   public String initUpdateCustomerForm(@PathVariable UUID customerId, Model model) {
       if(customerRepository.findById(customerId).isPresent())
          model.addAttribute("customer", customerRepository.findById(customerId).get());
       return "customers/createOrUpdateCustomer";
   }

    @PostMapping("/{beerId}/edit")
    public String processUpdationForm(@Valid Customer customer, BindingResult result) {
        if (result.hasErrors()) {
            return "beers/createOrUpdateCustomer";
        } else {
            //ToDO: Add Service
            Customer savedCustomer =  customerRepository.save(customer);
            return "redirect:/customers/" + savedCustomer.getId();
        }
    }

}
