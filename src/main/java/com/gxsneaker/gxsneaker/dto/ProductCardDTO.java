package com.gxsneaker.gxsneaker.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCardDTO {

    private Long id;

    private String tenSanPham;

    private String thuongHieu;

    private String danhMuc;

    private String anhDaiDien;

    private BigDecimal giaMin;
}