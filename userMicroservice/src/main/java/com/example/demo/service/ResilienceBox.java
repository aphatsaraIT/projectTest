package com.example.demo.service;

import com.example.demo.controller.UserController;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ResilienceBox {

    private RestTemplate restTemplate;
    private static final Logger logger = Logger.getLogger(UserController.class);
    private String circuitBreakerUrl = "http://localhost:8083/circuit-breaker";

    @CircuitBreaker(name = "accountServiceBreaker", fallbackMethod = "timeoutFallback")
    @TimeLimiter(name = "accountServiceTimeout")
    @Bulkhead(name = "accountBulkhead", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<ResponseEntity<List>> getCircuitBreaker() {
            restTemplate = new RestTemplate();
            String urlAccount = "http://localhost:8083/account";

            return CompletableFuture.supplyAsync(() ->
                restTemplate.exchange(
                        urlAccount,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List>() {}
                )
            );
    }

//    @CircuitBreaker(name = "accountServiceBreaker", fallbackMethod = "breakerFallback")
//    public ResponseEntity<String> getCircuitBreaker1() throws Exception {
////        return "test";
//        throw new Exception("po");
//    }
    public ResponseEntity<List> breakerFallback(Exception e) throws Exception {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    public ResponseEntity<List> timeoutFallback(Exception e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(null);
    }

}
