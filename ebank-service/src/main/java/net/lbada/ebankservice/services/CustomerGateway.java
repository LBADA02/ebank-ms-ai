package net.lbada.ebankservice.services;

import net.lbada.ebankservice.models.Customer;

public interface CustomerGateway {
    Customer getCustomerById(Long id);
}
