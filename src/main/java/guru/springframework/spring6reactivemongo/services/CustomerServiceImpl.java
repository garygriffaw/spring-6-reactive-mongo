package guru.springframework.spring6reactivemongo.services;

import guru.springframework.spring6reactivemongo.mappers.CustomerMapper;
import guru.springframework.spring6reactivemongo.model.CustomerDTO;
import guru.springframework.spring6reactivemongo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    @Override
    public Flux<CustomerDTO> listCustomers() {
        return customerRepository.findAll()
                .map(customerMapper::customerToCustomerDto);
    }
}
