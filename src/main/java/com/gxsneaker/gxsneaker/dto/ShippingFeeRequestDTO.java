package com.gxsneaker.gxsneaker.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShippingFeeRequestDTO {

    private String provinceCode;

    private String districtCode;

    private String wardCode;

    private String address;

    private List<DatHangItemDTO> items;
}