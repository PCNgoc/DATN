package com.gxsneaker.gxsneaker.mapper;

import com.gxsneaker.gxsneaker.dto.SanPhamDTO;
import com.gxsneaker.gxsneaker.entity.SanPham;

public class SanPhamMapper {

    public static SanPhamDTO toDTO(SanPham sp) {

        SanPhamDTO dto = new SanPhamDTO();

        dto.setId(sp.getId());

        dto.setIdThuongHieu(sp.getThuongHieu().getId());
        dto.setIdXuatXu(sp.getXuatXu().getId());
        dto.setIdChatLieu(sp.getChatLieu().getId());
        dto.setIdCoGiay(sp.getCoGiay().getId());
        dto.setIdDeGiay(sp.getDeGiay().getId());
        dto.setIdDanhMuc(sp.getDanhMuc().getId());

        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTenSanPham());

        dto.setGioiTinh(sp.getGioiTinh());

        dto.setMoTaNgan(sp.getMoTaNgan());
        dto.setMoTaChiTiet(sp.getMoTaChiTiet());

        dto.setTrangThai(sp.getTrangThai());

        dto.setNguoiTao(sp.getNguoiTao());
        dto.setNguoiCapNhat(sp.getNguoiCapNhat());

        return dto;
    }
}