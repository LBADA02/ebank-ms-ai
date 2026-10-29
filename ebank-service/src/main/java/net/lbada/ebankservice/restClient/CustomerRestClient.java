package net.lbada.ebankservice.restClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import net.lbada.ebankservice.models.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("customer-service")
public interface CustomerRestClient {
    @RequestMapping(method = RequestMethod.GET , value = "/customers/{id}")
    Customer getCustomerById(@PathVariable Long id);

}
