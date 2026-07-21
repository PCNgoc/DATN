package com.gxsneaker.gxsneaker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class HoaDonTaiQuayDTO {

    private Long id;

    private String maHoaDon;

    private Date ngayTao;

    private String tenKhachHang;

    private String soDienThoai;

    private String tenNhanVien;

    private BigDecimal tongTienHang;

    private BigDecimal soTienGiam;

    private BigDecimal tongTienThanhToan;

    private String trangThaiThanhToan;

    private String phuongThucThanhToan;

}