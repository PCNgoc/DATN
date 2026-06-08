package com.gxsneaker.gxsneaker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ThuongHieuDTO {

    private Long id;
    private String ma;
    private String ten;
    private String logo;
    private String website;
    private String moTa;
    private String quocGia;
    private Boolean trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
}
