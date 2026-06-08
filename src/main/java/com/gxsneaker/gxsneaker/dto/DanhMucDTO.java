package com.gxsneaker.gxsneaker.dto;

import lombok.Data;
import java.util.Date;

@Data
public class DanhMucDTO {

    private Long id;
    private String ma;
    private String ten;
    private String moTa;
    private Integer thuTuHienThi;
    private Boolean trangThai;
    private Date ngayTao;
}