package com.sec.lending.marketplace.repository;

import com.sec.lending.marketplace.entities.SecuritiesLendingOffer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface SecuritiesLendingOffersRepository extends CrudRepository<SecuritiesLendingOffer, Long> {
   List<SecuritiesLendingOffer> findByOfferedBy(String offeredBy);
}
