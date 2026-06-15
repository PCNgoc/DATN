package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phan_quyen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhanQuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_quyen")
    private String maQuyen;

    @Column(name = "ten_quyen")
    private String tenQuyen;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}