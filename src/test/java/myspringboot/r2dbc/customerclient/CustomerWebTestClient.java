package myspringboot.r2dbc.customerclient;

import myspringboot.r2dbc.customerclient.dto.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerWebTestClient {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getCustomers() {
        Flux<Customer> customerFlux = webTestClient.get()
                .uri("/customers/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Customer.class)
                .getResponseBody()
                .log();

        StepVerifier.create(customerFlux)
                .expectNextCount(5)
                .verifyComplete();
    }
}