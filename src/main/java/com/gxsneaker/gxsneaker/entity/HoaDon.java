package com.gxsneaker.gxsneaker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

    @Column(name = "id_khach_hang")
    private Long idKhachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_khach_hang",
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private KhachHang khachHang;

    private Long idNhanVien;

    @Column(name = "id_phieu_giam_gia")
    private Long idPhieuGiamGia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_phieu_giam_gia",
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private PhieuGiamGia phieuGiamGia;

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
    @Column(name = "dia_chi_nguoi_nhan")
    private String diaChiNguoiNhan;

    private String emailNguoiNhan;

    private String trangThai;

    private String ghiChu;

    @Column(name = "phuong_thuc_thanh_toan")
    private String phuongThucThanhToan;
// COD, PAYOS

    @Column(name = "trang_thai_thanh_toan")
    private String trangThaiThanhToan;
// CHUA_THANH_TOAN, CHO_THANH_TOAN, DA_THANH_TOAN, QUA_HAN

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "han_thanh_toan")
    private Date hanThanhToan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_thanh_toan")
    private Date ngayThanhToan;

    @Column(name = "payos_order_code")
    private Long payosOrderCode;

    @Column(name = "payment_link_id")
    private String paymentLinkId;

    @Column(name = "checkout_url", length = 2000)
    private String checkoutUrl;

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



    @OneToMany(mappedBy = "hoaDon")
    @JsonManagedReference
    private List<HoaDonChiTiet> hoaDonChiTiets;
}