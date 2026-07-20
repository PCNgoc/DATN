package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.ShippingFeeQuoteDTO;
import com.gxsneaker.gxsneaker.dto.ShippingFeeRequestDTO;

public interface ShippingFeeService {

    ShippingFeeQuoteDTO calculateFee(ShippingFeeRequestDTO request);
}