package com.gxsneaker.gxsneaker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GenerateVariantDTO {

    private Long idSanPham;

    private List<Long> mauSacIds;

    private List<Long> kichThuocIds;

    private Integer soLuongTon;

    private BigDecimal giaNhap;

    private BigDecimal giaBan;

    private Boolean trangThai;
}