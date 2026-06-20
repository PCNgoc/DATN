package com.gxsneaker.gxsneaker.dto;

import lombok.Data;

@Data
public class SanPhamDTO {

    private Long id;

    private Long idThuongHieu;
    private Long idXuatXu;
    private Long idChatLieu;
    private Long idCoGiay;
    private Long idDeGiay;
    private Long idDanhMuc;
    private String tenThuongHieu;
    private String tenDanhMuc;
    private String tenChatLieu;
    private String tenXuatXu;
    private String maSanPham;
    private String tenSanPham;
    private String gioiTinh;
    private String moTaNgan;
    private String moTaChiTiet;

    private String anhDaiDien;
    private Boolean trangThai;

    private String nguoiTao;
    private String nguoiCapNhat;

}