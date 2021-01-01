package com.sec.lending.marketplace.corda;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransactionResponse {
    private String transactionId;
    private boolean success;
    private String message;
}
