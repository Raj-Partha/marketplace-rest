package com.sec.lending.marketplace.repository;

import com.sec.lending.marketplace.entities.SecuritiesLendingRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SecuritiesLendingRequestRepository extends CrudRepository<SecuritiesLendingRequest, Long> {
    List<SecuritiesLendingRequest> findBySelectedLender(String selectedLender);

    List<SecuritiesLendingRequest> findBySelectedLenderAndStatus(String selectedLender, String status);

    List<SecuritiesLendingRequest> findByRequestedByAndStatus(String selectedLender, String status);
}
