package myspringboot.r2dbc.customerclient.controller;

import myspringboot.r2dbc.customerclient.dto.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private WebClient webClient;

//    @PostConstruct
//    public void init() {
//        webClient = WebClient.builder().baseUrl("http://localhost:8080/router/customers")
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
//    }

    @GetMapping
    public Flux<Customer> getAllCustomer() {
        return webClient.get().retrieve().bodyToFlux(Customer.class);
    }

    @GetMapping("/{id}")
    public Mono<Object> getCustomerById(@PathVariable Long id) {
        return webClient.get().uri("/{id}", id)
//                .retrieve().bodyToMono(Customer.class);
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(Customer.class);
                    } else if (response.statusCode().is4xxClientError()) {
                        return Mono.just("Error response : " + response.statusCode().value());
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }

    @PostMapping
    public Mono<Customer> insertCustomer(@RequestBody Customer customer) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), Customer.class)
                .retrieve().bodyToMono(Customer.class);
    }

    @PutMapping("/{id}")
    public Mono<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer){
        return webClient.put()
                .uri("/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), Customer.class)
                .retrieve().bodyToMono(Customer.class);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCustomer(@PathVariable Long id) {
        return webClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
