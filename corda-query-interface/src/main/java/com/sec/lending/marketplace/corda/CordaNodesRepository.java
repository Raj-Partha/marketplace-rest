package com.sec.lending.marketplace.corda;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CordaNodesRepository extends CrudRepository<CordaNodes, Long> {
    List<CordaNodes> findByShortName(String shortName);
    CordaNodes findFirstByShortName(String shortName);
}
