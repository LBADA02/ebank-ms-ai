package net.lbada.customerservice.services.impliments;

import net.lbada.customerservice.entitis.Customer;
import net.lbada.customerservice.repositories.CustomerRepo;
import net.lbada.customerservice.services.CustomerService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImp implements CustomerService {

    private CustomerRepo customerRepository;

    public CustomerServiceImp(CustomerRepo customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @McpTool(description = "Get All Customer")
    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    @McpTool(description = "Find a Customer By Id")
    public Customer findCustomerById(@McpToolParam(description = "The customer id") Long id) {
        return customerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Customer Not Found"));
    }

    @Override
    @McpTool(description = "save a customer")
    public Customer saveCustomer(@McpToolParam(description = "The customer to save (name , email)") Customer customer) {
        return customerRepository.save(customer);
    }


}
