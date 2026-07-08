package com.gxsneaker.gxsneaker.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;

    private String maHoaDon;

    private String trangThai;

    private Date ngayDatHang;

    private String tenNguoiNhan;

    private String soDienThoai;

    private String diaChi;

    private BigDecimal tongTien;

    private String maVanDon;

    private String loaiDon;

    private String phuongThucThanhToan;

    private String trangThaiThanhToan;

    private String ghiChu;

    private BigDecimal tongTienHang;

    private BigDecimal phiVanChuyen;

    private BigDecimal soTienGiam;

    private String maPhieuGiamGia;

    private String tenPhieuGiamGia;


    //admin
    private String emailNguoiNhan;

    private Date ngayXacNhan;

    private Date ngayGiaoHang;

    private Date ngayHoanThanh;

    private Date ngayHuy;

    private String lyDoHuy;

    private List<OrderItemResponseDTO> items;
}