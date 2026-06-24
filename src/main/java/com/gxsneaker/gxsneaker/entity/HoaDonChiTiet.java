package com.gxsneaker.gxsneaker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
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


    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    @JsonBackReference
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "id_chi_tiet_san_pham")
    @JsonIgnore
    private ChiTietSanPham chiTietSanPham;

    private Integer soLuong;

    private BigDecimal donGia;

    private BigDecimal thanhTien;
}