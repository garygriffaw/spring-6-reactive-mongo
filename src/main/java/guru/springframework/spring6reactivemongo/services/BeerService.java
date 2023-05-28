package guru.springframework.spring6reactivemongo.services;

import guru.springframework.spring6reactivemongo.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {

    Flux<BeerDTO> listBeers();

    Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDTO);

    Mono<BeerDTO> getById(String beerId);

    Mono<BeerDTO> updateBeer(String beerId, BeerDTO beerDTO);
}