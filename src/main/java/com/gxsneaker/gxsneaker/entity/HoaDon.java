package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "HOA_DON")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idKhachHang;

    private Long idNhanVien;

    private Long idPhieuGiamGia;

    private Long idDonViVanChuyen;

    private String maHoaDon;

    private String maVanDon;

    private String loaiDon;

    private BigDecimal tongTienHang;

    private BigDecimal soTienGiam;

    private BigDecimal phiVanChuyen;

    private BigDecimal tongTienThanhToan;

    private String tenNguoiNhan;

    private String soDienThoaiNguoiNhan;

    private String emailNguoiNhan;

    private String trangThai;

    private String ghiChu;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayDatHang;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayXacNhan;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayGiaoHang;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayHoanThanh;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayHuy;

    private String lyDoHuy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao;

    private String nguoiTao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayCapNhat;

    private String nguoiCapNhat;
}