package com.gxsneaker.gxsneaker.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChiTietSanPhamDTO {

    private Long id;

    private Long idSanPham;

    private Long idMauSac;

    private Long idKichThuoc;

    private String maChiTiet;

    private Integer soLuongTon;

    private BigDecimal giaNhap;

    private BigDecimal giaBan;

    private Boolean trangThai;

    // hiển thị

    private String tenSanPham;

    private String tenMauSac;

    private Integer size;
}