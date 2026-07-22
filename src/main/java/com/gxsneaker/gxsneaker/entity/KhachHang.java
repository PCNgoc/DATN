package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import com.gxsneaker.gxsneaker.enums.HangThanhVien;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_khach_hang")
    private String maKhachHang;

    @Column(name = "ho_ten", columnDefinition = "NVARCHAR(255)")
    private String hoTen;

    private String email;

    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    private String avatar;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "da_xac_thuc")
    private Boolean daXacThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "hang_thanh_vien")
    private HangThanhVien hangThanhVien;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_cap_nhat")
    private Date ngayCapNhat;

    @PrePersist
    public void prePersist() {
        ngayTao = new Date();
        if (hangThanhVien == null) {
            hangThanhVien = HangThanhVien.BRONZE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = new Date();
    }
}