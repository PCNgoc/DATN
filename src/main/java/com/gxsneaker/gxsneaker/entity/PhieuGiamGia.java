package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import com.gxsneaker.gxsneaker.enums.HangThanhVien;
import com.gxsneaker.gxsneaker.enums.KieuPhieuGiamGia;

@Entity
@Table(name = "phieu_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phieu")
    private String maPhieu;

    @Column(name = "ten_phieu", columnDefinition = "NVARCHAR(255)")
    private String tenPhieu;

    @Column(name = "loai_giam_gia")
    private Boolean loaiGiamGia;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "gia_tri_don_hang_toi_thieu")
    private BigDecimal giaTriDonHangToiThieu;

    @Column(name = "gia_tri_giam_toi_da")
    private BigDecimal giaTriGiamToiDa;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Enumerated(EnumType.STRING)
    @Column(name = "kieu_phieu")
    private KieuPhieuGiamGia kieuPhieu;

    @Enumerated(EnumType.STRING)
    @Column(name = "dieu_kien_hang_thanh_vien")
    private HangThanhVien dieuKienHangThanhVien;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_ket_thuc")
    private Date ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_cap_nhat")
    private Date ngayCapNhat;

    @PrePersist
    public void prePersist() {
        ngayTao = new Date();
        if (trangThai == null) {
            trangThai = true;
        }
        if (kieuPhieu == null) {
            kieuPhieu = KieuPhieuGiamGia.PUBLIC;
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = new Date();
    }
}
