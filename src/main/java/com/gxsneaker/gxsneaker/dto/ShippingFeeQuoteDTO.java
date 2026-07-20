package com.gxsneaker.gxsneaker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeQuoteDTO {

    private String provider;

    private String quoteId;

    private BigDecimal fee;

    private String currency;

    private Date expiresAt;
}