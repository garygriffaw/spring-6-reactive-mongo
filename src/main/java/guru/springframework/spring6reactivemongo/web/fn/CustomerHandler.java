package guru.springframework.spring6reactivemongo.web.fn;

import guru.springframework.spring6reactivemongo.model.CustomerDTO;
import guru.springframework.spring6reactivemongo.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {
    private final CustomerService customerService;

    public Mono<ServerResponse> listCustomers(ServerRequest request) {
        Flux<CustomerDTO> flux;

        flux = customerService.listCustomers();

        return ServerResponse.ok()
                .body(flux, CustomerDTO.class);
    }
}
