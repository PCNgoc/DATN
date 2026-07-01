package com.gxsneaker.gxsneaker.dto;

import lombok.Data;

import java.util.List;

@Data
public class DatHangRequestDTO {

    private Long idKhachHang;

    private String tenNguoiNhan;

    private String soDienThoai;

    private String diaChi;

    private String ghiChu;

    private String maPhieuGiamGia;

    private String phuongThucThanhToan;

    private List<DatHangItemDTO> items;
}