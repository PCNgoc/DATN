package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "THUONG_HIEU")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma", nullable = false, unique = true, length = 20)
    private String ma;

    @Column(name = "ten", nullable = false, length = 100)
    private String ten;

    private String logo;
    private String website;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "quoc_gia")
    private String quocGia;

    private Boolean trangThai = true;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        this.ngayTao = LocalDateTime.now();
        this.trangThai = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}