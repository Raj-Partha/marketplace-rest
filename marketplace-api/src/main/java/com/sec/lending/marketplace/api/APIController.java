package com.sec.lending.marketplace.api;

import com.google.gson.Gson;
import com.sec.lending.marketplace.corda.*;
import com.sec.lending.marketplace.entities.SecuritiesLendingOffer;
import com.sec.lending.marketplace.entities.SecuritiesLendingRequest;
import com.sec.lending.marketplace.repository.SecuritiesLendingOffersRepository;
import com.sec.lending.marketplace.repository.SecuritiesLendingRequestRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping({"/api"})
@Log4j2
public class APIController {
    @Autowired
    CordaHandler cordaHandler;

    @Autowired
    private SecuritiesLendingRequestRepository requestRepository;

    @Autowired
    private SecuritiesLendingOffersRepository offersRepository;
    @Autowired
    private CordaNodesRepository cordaNodesRepository;


    @GetMapping("/request/list")
    public List<SecuritiesLendingRequest> getAllRequests() {
        return (List<SecuritiesLendingRequest>) requestRepository.findAll();
    }


    @PutMapping("/request/approve-request")
    public ResponseEntity<String> approveRequest(@RequestParam String loginId, @RequestBody SecuritiesLendingRequest request) throws UnsupportedEncodingException {
        try {

            HttpEntity<String> responseSelfCreate = cordaHandler.selfIssue(loginId, request.getNoOfStocks(), request.getSymbol());
            log.info("self creation completed " + responseSelfCreate);
            TransactionResponse responseValue = cordaHandler.createSecLedger(loginId, request.getRequestedBy(), request.getNoOfStocks(), request.getSymbol());
            if (responseValue.isSuccess()) {
                log.info("Contract creation completed " + responseValue);
                request.setStatus("Approved");
                request.setTransactionId(responseValue.getTransactionId());
//            SecuritiesLendingOffer offeredBy = offersRepository.findByOfferedBy(request.getSelectedLender()).get(0);
//            offeredBy.setNoOfStocks(offeredBy.getNoOfStocks() - request.getNoOfStocks());
//            offersRepository.save(offeredBy);
                requestRepository.save(request);
                return ResponseEntity.ok().body(new Gson().toJson(requestRepository.save(request)));
            } else {
                request.setStatus("Insufficient Collateral");
                requestRepository.save(request);
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(new Gson().toJson(responseValue));
            }
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("error", e.getMessage()).body(null);
        }
    }

    @PutMapping("/request/close-contract")
    public SecuritiesLendingRequest closeContract(@RequestParam String loginId, @RequestBody SecuritiesLendingRequest request) throws UnsupportedEncodingException {
        HttpEntity<TransactionResponse> responsecreateSecLedger = cordaHandler.closeSecLedger(loginId, request.getRequestedBy(), request.getNoOfStocks(), request.getSymbol());
        log.info("self creation completed " + responsecreateSecLedger);
        request.setStatus("Closed");
        request.setTransactionId(responsecreateSecLedger.getBody().getTransactionId());
        return requestRepository.save(request);
    }

    @PutMapping("/request/add-request")
    public SecuritiesLendingRequest updateRequest(@RequestBody SecuritiesLendingRequest request) throws UnsupportedEncodingException {
        return requestRepository.save(request);
    }

    @GetMapping("/request/pending-approvals")
    public List<SecuritiesLendingRequest> getAllPendingApprovalRequests(@RequestParam String party) throws
            UnsupportedEncodingException {
        return requestRepository.findBySelectedLenderAndStatus(java.net.URLDecoder.decode(party, "UTF-8"), "Created");
    }

    @GetMapping("/request/completed-tranactions")
    public List<SecuritiesLendingRequest> getAllApprovedRequests(@RequestParam String party) throws
            UnsupportedEncodingException {
        return requestRepository.findBySelectedLenderAndStatus(java.net.URLDecoder.decode(party, "UTF-8"), "Approved");
    }

    @GetMapping("/request/completed-requested-transactions")
    public List<SecuritiesLendingRequest> getAllCompltedTransactionsByRequester(@RequestParam String party) throws
            UnsupportedEncodingException {
        return requestRepository.findByRequestedByAndStatus(java.net.URLDecoder.decode(party, "UTF-8"), "Approved");
    }


    @GetMapping("/offer/list")
    public List<SecuritiesLendingOffer> getAllOffers() {
        return (List<SecuritiesLendingOffer>) offersRepository.findAll();
    }

    @PutMapping("/offer/add-request")
    public SecuritiesLendingOffer updateOffer(@RequestBody SecuritiesLendingOffer offer) {
        return offersRepository.save(offer);
    }


    @GetMapping("/offer/eligible-parties")
    public List<String> getEligibleParties(@RequestParam String loginId, @RequestParam String noOfStocks, @RequestParam String symbol) {
        List<String> eligibleParties = new ArrayList<>();
        int noOfStocksValue = Integer.valueOf(noOfStocks);
        offersRepository.findAll().forEach(offer -> {
            if (offer.getSymbol().equals(symbol) && noOfStocksValue <= offer.getNoOfStocks() && !offer.getOfferedBy().equals(loginId)) {
                if (!eligibleParties.contains(offer.getOfferedBy())) {
                    eligibleParties.add(offer.getOfferedBy());
                }
            }
        });
        return eligibleParties;
    }

    @GetMapping("/login/roles")
    public List<Roles> getRoles() {
        return Arrays.asList(Roles.values());
    }


    @GetMapping("/login/get-all-users")
    public Iterable<CordaNodes> getAllUsers() {
        List<CordaNodes> users = new ArrayList<>();
        cordaNodesRepository.findAll().forEach(nodeDetails -> {
            if (!CollectionUtils.isEmpty(nodeDetails.getRoles())) {
                CordaNodes node = CordaNodes.builder().id(nodeDetails.getId()).shortName(nodeDetails.getShortName()).roles(nodeDetails.getRoles()).build();
                users.add(node);
            } else {
                Roles[] roles = {Roles.Admin};
                CordaNodes admin = CordaNodes.builder().id(nodeDetails.getId()).shortName("Admin").roles(Arrays.asList(roles)).build();
                users.add(admin);
            }
        });

        return users;
    }

    @GetMapping("/admin/get-all-nodes")
    public Iterable<CordaNodes> getAllNodes() {
        return cordaNodesRepository.findAll();
    }


    @PutMapping("/admin/add-node")
    public CordaNodes updateRequest(@RequestBody CordaNodes request) {
        return cordaNodesRepository.save(request);
    }

}
