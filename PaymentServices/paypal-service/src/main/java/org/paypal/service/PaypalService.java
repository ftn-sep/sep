package org.paypal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.paypal.config.PaypalConfig;
import org.paypal.dto.payment.*;
import org.paypal.model.PaypalPayment;
import org.paypal.repository.PaypalPaymentRepository;
import org.sep.dto.card.PaymentUrlAndIdRequest;
import org.sep.dto.card.PaymentUrlIdResponse;
import org.sep.dto.card.TransactionDetails;
import org.sep.enums.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class PaypalService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private HttpClient httpClient;
    @Autowired
    private PaypalConfig paypalConfig;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaypalPaymentRepository paypalPaymentRepository;

    @Autowired
    public PaypalService() {
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    public String hello() {
        return "Hello from Paypal";
    }

    public AccessTokenResponseDTO getAccessToken() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(paypalConfig.getBaseUrl() + "/v1/oauth2/token"))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, encodeBasicCredentials())
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en_US")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, AccessTokenResponseDTO.class);
    }

    public ClientTokenDTO getClientToken() throws Exception {
        var accessTokenDto = getAccessToken();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(paypalConfig.getBaseUrl() + "/v1/identity/generate-token"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en_US")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();

        return objectMapper.readValue(content, ClientTokenDTO.class);
    }

    public PaymentUrlIdResponse createOrder(PaymentUrlAndIdRequest paymentInfo) throws Exception {
        MoneyDTO money = new MoneyDTO();
        money.setValue(Double.toString(paymentInfo.getAmount()));
        money.setCurrencyCode("EUR");
        PurchaseUnitDTO purchaseUnit = new PurchaseUnitDTO();
        purchaseUnit.setAmount(money);
        OrderDTO order = new OrderDTO();
        order.setPurchaseUnits(List.of(purchaseUnit));
        order.setIntent(OrderIntent.CAPTURE);
        var appContext = new PaypalAppContextDTO();
        appContext.setReturnUrl("http://localhost:8010/api/paypal/success");
        appContext.setCancelUrl(paymentInfo.getErrorUrl());
        order.setApplicationContext(appContext);
        var accessTokenDto = getAccessToken();
        var payload = objectMapper.writeValueAsString(order);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(paypalConfig.getBaseUrl() + "/v2/checkout/orders"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        var orderResponse = objectMapper.readValue(content, OrderResponseDTO.class);
        saveOrder(orderResponse, order, paymentInfo);
        PaymentUrlIdResponse paymentUrlIdResponse = new PaymentUrlIdResponse(orderResponse.getLinks().get(1).getHref(),
                paypalPaymentRepository.findByPaypalOrderId(orderResponse.getId()).getId(), paymentInfo.getAmount());

        return paymentUrlIdResponse;
    }

    public void saveOrder(OrderResponseDTO orderResponseDTO, OrderDTO orderDTO, PaymentUrlAndIdRequest paymentInfo) {
        var payment = new PaypalPayment();
        payment.setPaypalOrderId(orderResponseDTO.getId());
        payment.setAmount(Double.parseDouble(orderDTO.getPurchaseUnits().get(0).getAmount().getValue()));
        payment.setPaypalOrderStatus(orderResponseDTO.getStatus().toString());
        payment.setMerchantOrderId(paymentInfo.getMerchantOrderId());
        paypalPaymentRepository.save(payment);
    }

    public void confirmOrder(String paymentId) throws Exception {
        var accessTokenDto = getAccessToken();
        var payment = paypalPaymentRepository.findByPaypalOrderId(paymentId);
        var request = HttpRequest.newBuilder()
                .uri(URI.create(paypalConfig.getBaseUrl() + "/v2/checkout/orders/" + paymentId + "/capture"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        payment.setPaypalOrderStatus(OrderStatus.COMPLETED.toString());
        paypalPaymentRepository.save(payment);
        sendTransactionDetailsToPsp(payment);
    }

    private void sendTransactionDetailsToPsp(PaypalPayment payment) {
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .merchantOrderId(payment.getMerchantOrderId())
                .paymentId(payment.getId())
                .paymentStatus(PaymentStatus.DONE)  // todo: da li ce uvek biti uspesno?
                .build();

        webClientBuilder.build().post()
                .uri("http://psp-service/api/psp/transaction-details")
                .header(org.apache.http.HttpHeaders.CONTENT_TYPE, jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionDetails), TransactionDetails.class)
                .exchange().toFuture();
    }

    private String encodeBasicCredentials() {
        var input = paypalConfig.getClientId() + ":" + paypalConfig.getSecret();
        return "Basic " + Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
