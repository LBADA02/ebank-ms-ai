package net.lbada.customerservice.controllers;

import net.lbada.customerservice.entitis.Customer;
import net.lbada.customerservice.services.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("customers")
public class CustomerRestController {

    private CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    public List<Customer> getAllCustomer(){
        return customerService.getAllCustomer();

    }
    @GetMapping("/{id}")
    public Customer findCustomerById(@PathVariable Long id){
        return  customerService.findCustomerById(id);

    }
    @PostMapping()
    public Customer saveCustomer(@RequestBody Customer customer){
        return customerService.saveCustomer(customer);
    }



}
