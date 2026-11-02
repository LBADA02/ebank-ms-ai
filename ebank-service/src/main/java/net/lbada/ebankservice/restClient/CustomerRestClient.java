package net.lbada.ebankservice.restClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import net.lbada.ebankservice.models.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("customer-service")
public interface CustomerRestClient {
    @RequestMapping(method = RequestMethod.GET , value = "/customers/{id}")
    Customer getCustomerById(@PathVariable Long id);

    @GetMapping("/customers/batch")
    public List<Customer> getCustomersByIds(@RequestBody List<Long> customersIds);

}
