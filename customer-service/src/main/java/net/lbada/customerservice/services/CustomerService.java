package net.lbada.customerservice.services;

import net.lbada.customerservice.entitis.Customer;

import java.util.List;

public interface CustomerService {

    public List<Customer> getAllCustomer();
    public Customer findCustomerById(Long id);
    public Customer saveCustomer(Customer customer);
}
