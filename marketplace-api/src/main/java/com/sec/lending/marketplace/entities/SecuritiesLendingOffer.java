package com.sec.lending.marketplace.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public class SecuritiesLendingOffer {

    @Id
    @GeneratedValue
    private long offerId;
    @NotNull
    private String symbol;
    @Min(value = 1L, message = "The value must be positive")
    private Long noOfStocks;
    @Min(value = 0, message = "The value must be positive")
    private Double fees;
    @CreationTimestamp
    private LocalDateTime createDateTime;
    private String offeredBy;

}
