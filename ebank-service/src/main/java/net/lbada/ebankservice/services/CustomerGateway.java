package net.lbada.ebankservice.services;

import net.lbada.ebankservice.models.Customer;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CustomerGateway {
    Customer getCustomerById(Long id);
    List<Customer> getCustomersByIds(List<Long> customersIds);
}
