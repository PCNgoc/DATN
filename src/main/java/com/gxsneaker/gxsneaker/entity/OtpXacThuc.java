package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_xac_thuc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpXacThuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "ma_otp")
    private String maOtp;

    @Column(name = "loai_otp")
    private String loaiOtp;

    @Column(name = "thoi_gian_het_han")
    private LocalDateTime thoiGianHetHan;

    @Column(name = "da_su_dung")
    private Boolean daSuDung;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}