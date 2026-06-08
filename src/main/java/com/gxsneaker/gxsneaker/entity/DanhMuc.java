package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "DANH_MUC")
@Data
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ma;
    private String ten;
    private String moTa;
    private Integer thuTuHienThi;
    private Boolean trangThai;

    private Date ngayTao;
}
