package com.gxsneaker.gxsneaker.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DON_VI_VAN_CHUYEN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonViVanChuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_don_vi")
    private String maDonVi;

    @Column(name = "ten_cong_ty")
    private String tenCongTy;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    private String website;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}