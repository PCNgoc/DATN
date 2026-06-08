package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "LICH_SU_TRANG_THAI_HOA_DON")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuTrangThaiHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idHoaDon;

    private String trangThaiCu;

    private String trangThaiMoi;

    private String nguoiThucHien;

    @Temporal(TemporalType.TIMESTAMP)
    private Date thoiGian;

    private String ghiChu;
}