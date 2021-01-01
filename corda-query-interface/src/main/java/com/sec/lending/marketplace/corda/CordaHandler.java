package com.sec.lending.marketplace.corda;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;

@Service
@Log4j2
public class CordaHandler {

    @Autowired
    private CordaNodesRepository cordaNodesRepository;

    public HttpEntity<String> selfIssue(String loginId, long noOfStocks, String symbol) {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        log.info("Starting the self creation " + loginId);
        CordaNodes requestedNode = cordaNodesRepository.findFirstByShortName(loginId);
        log.info("rest end point selected " + requestedNode);
        UriComponentsBuilder builderSelf = UriComponentsBuilder.fromHttpUrl(requestedNode.getRestEndPoint() + "/api/template/selfIssueSec")
                .queryParam("noOfStocks", noOfStocks)
                .queryParam("symbol", symbol);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        return template.exchange(
                builderSelf.toUriString(),
                HttpMethod.PUT,
                entity,
                String.class);
    }


    public TransactionResponse createSecLedger(String loginId, String requestedBy, long noOfStocks, String symbol) throws UnsupportedEncodingException {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        CordaNodes requestedByNode = cordaNodesRepository.findFirstByShortName(requestedBy);
        log.info("Contract creation started " + loginId);
        CordaNodes requestedNode = cordaNodesRepository.findFirstByShortName(loginId);

        UriComponentsBuilder builderCreate = UriComponentsBuilder.fromHttpUrl(requestedNode.getRestEndPoint() + "/api/template/createSecLedger")
                .queryParam("partyName", requestedByNode.getCordaName(), "UTF-8")
                .queryParam("noOfStocks", noOfStocks)
                .queryParam("symbol", symbol);
        log.info("Querying " + requestedNode.getRestEndPoint() + "/api/template/createSecLedger" + builderCreate);
        ResponseEntity<TransactionResponse> exchange = template.exchange(
                builderCreate.toUriString(),
                HttpMethod.PUT,
                entity,
                TransactionResponse.class);
        return exchange.getBody();
    }

    public HttpEntity<TransactionResponse> closeSecLedger(String loginId, String requestedBy, long noOfStocks, String symbol) throws UnsupportedEncodingException {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);


        CordaNodes requestedByNode = cordaNodesRepository.findFirstByShortName(requestedBy);
        CordaNodes requestedNode = cordaNodesRepository.findFirstByShortName(loginId);
        UriComponentsBuilder builderCreate = UriComponentsBuilder.fromHttpUrl(requestedNode.getRestEndPoint() + "/api/template/closeSecLedger")
                .queryParam("partyName", requestedByNode.getCordaName(), "UTF-8")
                .queryParam("noOfStocks", noOfStocks)
                .queryParam("symbol", symbol);

        return template.exchange(
                builderCreate.toUriString(),
                HttpMethod.PUT,
                entity,
                TransactionResponse.class);
    }

}
