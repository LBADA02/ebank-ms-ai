package net.lbada.ebankservice.services.impliments;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.lbada.ebankservice.models.Customer;
import net.lbada.ebankservice.restClient.CustomerRestClient;
import net.lbada.ebankservice.services.CustomerGateway;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerGatewayImpl implements CustomerGateway {
    private CustomerRestClient customerRestClient;

    public CustomerGatewayImpl(CustomerRestClient customerRestClient) {
        this.customerRestClient = customerRestClient;
    }

    @Override
    @CircuitBreaker(
            name = "customerService",
            fallbackMethod = "customerServiceFallback"
    )
    public Customer getCustomerById(Long id) {
        return customerRestClient.getCustomerById(id);
    }

    @Override
    public List<Customer> getCustomersByIds(List<Long> customersIds) {
        return  customerRestClient.getCustomersByIds(customersIds);
    }


    private Customer customerServiceFallback(Long id, Throwable ex) {
        return new Customer(id,"No Name","No Email");
    }
}
