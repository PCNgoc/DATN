package com.gxsneaker.gxsneaker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTietBanHangDTO {

    private Long id;

    private Long chiTietSanPhamId;

    private String tenSanPham;

    private String mauSac;

    private Integer size;

    private Integer soLuong;

    private BigDecimal donGia;

    private BigDecimal thanhTien;

    private String anh;
}