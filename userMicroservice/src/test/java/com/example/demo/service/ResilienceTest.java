package com.example.demo.service;

import com.example.demo.pojo.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
@RestClientTest(ResilienceBox.class)
public class ResilienceTest {
    @Mock
    private RestTemplate restTemplate;
    private ResilienceBox resilienceBox;
    private MockRestServiceServer mockServer;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        resilienceBox = new ResilienceBox();
        ReflectionTestUtils.setField(resilienceBox, "restTemplate", restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void getCircuitBreakerTest_success() {
        List<Account> accounts = new ArrayList<>();
        Account account1 = new Account();
        account1.setAccount_id(1);
        account1.setAccount_number("67968");
        account1.setAccount_type("Fixed Deposit Account");
        accounts.add(account1);

        Account account2 = new Account();
        account2.setAccount_id(2);
        account2.setAccount_number("0381732925");
        account2.setAccount_type("Fixed Deposit Account");
        accounts.add(account2);

        ResponseEntity<List> responseEntity = ResponseEntity.ok(accounts);

        doReturn(responseEntity).when(restTemplate).exchange(
                eq("http://localhost:8083/account"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );

        CompletionStage<ResponseEntity<List>> result = resilienceBox.getCircuitBreaker();

        CompletableFuture<ResponseEntity<List>> completableFutureResult = result.toCompletableFuture();
        assertEquals(responseEntity.getStatusCode(), completableFutureResult.join().getStatusCode());
        for (int i = 0; i < accounts.size(); i++) {
            Account expectedAccount = accounts.get(i);
            Account actualAccount = accounts.get(i);

            assertEquals(expectedAccount.getAccount_id(), actualAccount.getAccount_id());
            assertEquals(expectedAccount.getAccount_number(), actualAccount.getAccount_number());
            assertEquals(expectedAccount.getAccount_type(), actualAccount.getAccount_type());
        }
    }

    @Test
    public void getCircuitBreaker_serviceDown() {
        String url = "http://localhost:8083/account";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<List> errorResponse = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();

        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        CompletableFuture<ResponseEntity<List>> result = resilienceBox.getCircuitBreaker();

        ResponseEntity<List> actualResponse = result.join();
//        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, actualResponse.getStatusCode());
//
//        mockServer.verify();
    }
}
