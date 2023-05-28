package guru.springframework.spring6reactivemongo.services;

import guru.springframework.spring6reactivemongo.domain.Beer;
import guru.springframework.spring6reactivemongo.mappers.BeerMapper;
import guru.springframework.spring6reactivemongo.mappers.BeerMapperImpl;
import guru.springframework.spring6reactivemongo.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Autowired
    BeerMapper beerMapper;

    BeerDTO beerDTO;

    @BeforeEach
    void setUp() {
        beerDTO = beerMapper.beerToBeerDto(getTestBeer());
    }

    @Test
    void testListBeers() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<List<BeerDTO>> atomicList = new AtomicReference<>();

        Flux<BeerDTO> fluxBeerDtos = beerService.listBeers();
        Mono<List<BeerDTO>> listMono= fluxBeerDtos.collectList();

        listMono.subscribe(list -> {
            atomicBoolean.set(true);
            atomicList.set(list);
//            list.forEach(dto -> {
//                System.out.println(dto.toString());
//            });
        });

        await().untilTrue(atomicBoolean);

        List<BeerDTO> retrievedList = atomicList.get();
        assertThat(retrievedList).isNotNull();
    }

    @Test
    @DisplayName("Test Save Beer Using Subscriber")
    @Order(1)
    void testSaveBeerSubscriber() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<BeerDTO> atomicDto = new AtomicReference<>();

        Mono<BeerDTO> savedMono = beerService.saveBeer(Mono.just(beerDTO));

        savedMono.subscribe(savedDto -> {
            System.out.println(savedDto.getId());
            atomicBoolean.set(true);
            atomicDto.set(savedDto);
        });

        await().untilTrue(atomicBoolean);

        BeerDTO persistedDto = atomicDto.get();
        assertThat(persistedDto).isNotNull();
        assertThat(persistedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Save Beer Using Block")
    void testSaveBeerBlock() {
        BeerDTO savedDto = beerService.saveBeer(Mono.just(getTestBeerDto())).block();
        System.out.println(savedDto.getId());

        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    void testUpdateBeerBlock() {
        final String updateName = "Updated name";
        BeerDTO savedBeerDto = getSavedBeerDto();
        savedBeerDto.setBeerName(updateName);

        BeerDTO updatedDto = beerService.updateBeer(savedBeerDto.getId(), savedBeerDto).block();

        BeerDTO fetchedDto = beerService.getById(updatedDto.getId()).block();
        assertThat(fetchedDto.getBeerName()).isEqualTo(updateName);
    }

    @Test
    void testUpdateBeerSubscriber() {
        final String updateName = "Updated name";
        BeerDTO savedBeerDto = getSavedBeerDto();
        savedBeerDto.setBeerName(updateName);

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<BeerDTO> atomicDto = new AtomicReference<>();

        Mono<BeerDTO> updatedMono = beerService.updateBeer(savedBeerDto.getId(), savedBeerDto);

        updatedMono.subscribe(updatedBeerDto -> {
           atomicBoolean.set(true);
           atomicDto.set(updatedBeerDto);
        });

        await().untilTrue(atomicBoolean);

        BeerDTO persistedBeerDto = atomicDto.get();
        assertThat(persistedBeerDto.getBeerName()).isEqualTo(updateName);
    }

    @Test
    void testPatchBeerBlock() {
        final String updateName = "Patched name";
        BeerDTO savedBeerDto = getSavedBeerDto();
        savedBeerDto.setBeerName(updateName);

        BeerDTO updatedDto = beerService.patchBeer(savedBeerDto.getId(), savedBeerDto).block();

        BeerDTO fetchedDto = beerService.getById(updatedDto.getId()).block();
        assertThat(fetchedDto.getBeerName()).isEqualTo(updateName);
    }

    @Test
    void testDeleteBeer() {
        BeerDTO beerToDelete = getSavedBeerDto();

        beerService.deleteBeerById(beerToDelete.getId()).block();

        Mono<BeerDTO> expectedEmptyBeerMono = beerService.getById(beerToDelete.getId());

        BeerDTO emptyBeer = expectedEmptyBeerMono.block();

        assertThat(emptyBeer).isNull();
    }

    public BeerDTO getSavedBeerDto() {
        return beerService.saveBeer(Mono.just(getTestBeerDto())).block();
    }

    public static BeerDTO getTestBeerDto() {
        return new BeerMapperImpl().beerToBeerDto(getTestBeer());
    }

    public static Beer getTestBeer() {
        return Beer.builder()
                .beerName("New beer")
                .beerStyle("IPA")
                .price(new BigDecimal("9.99"))
                .quantityOnHand(20)
                .upc("1234")
                .build();
    }

}