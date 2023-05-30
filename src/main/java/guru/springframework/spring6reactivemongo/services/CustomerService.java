package guru.springframework.spring6reactivemongo.services;

import guru.springframework.spring6reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;

public interface CustomerService {
    Flux<CustomerDTO> listCustomers();
}
