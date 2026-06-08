package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "HOA_DON_CHI_TIET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idHoaDon;

    private Long idChiTietSanPham;

    private Integer soLuong;

    private BigDecimal donGia;

    private BigDecimal thanhTien;
}