package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "phieu_giam_gia_ca_nhan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaKhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_nhan")
    private Date ngayNhan;

    @Column(name = "da_su_dung")
    private Boolean daSuDung;

    @PrePersist
    public void prePersist() {
        ngayNhan = new Date();
        if (daSuDung == null) {
            daSuDung = false;
        }
    }
}
