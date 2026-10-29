package net.lbada.customerservice.services.impliments;

import net.lbada.customerservice.entitis.Customer;
import net.lbada.customerservice.repositories.CustomerRepo;
import net.lbada.customerservice.services.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImp implements CustomerService {

    private CustomerRepo customerRepository;

    public CustomerServiceImp(CustomerRepo customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Customer Not Found"));
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }


}
