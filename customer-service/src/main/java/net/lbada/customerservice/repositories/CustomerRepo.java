package net.lbada.customerservice.repositories;

import net.lbada.customerservice.entitis.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepo extends JpaRepository<Customer , Long> {
}
