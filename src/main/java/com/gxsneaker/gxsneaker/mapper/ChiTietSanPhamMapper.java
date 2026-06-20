package com.gxsneaker.gxsneaker.mapper;

import com.gxsneaker.gxsneaker.dto.ChiTietSanPhamDTO;
import com.gxsneaker.gxsneaker.entity.ChiTietSanPham;

public class ChiTietSanPhamMapper {

    public static ChiTietSanPhamDTO toDTO(
            ChiTietSanPham ctsp
    ){

        ChiTietSanPhamDTO dto =
                new ChiTietSanPhamDTO();

        dto.setId(ctsp.getId());

        dto.setIdSanPham(
                ctsp.getSanPham().getId()
        );

        dto.setIdMauSac(
                ctsp.getMauSac().getId()
        );

        dto.setIdKichThuoc(
                ctsp.getKichThuoc().getId()
        );

        dto.setMaChiTiet(
                ctsp.getMaChiTiet()
        );

        dto.setSoLuongTon(
                ctsp.getSoLuongTon()
        );

        dto.setGiaNhap(
                ctsp.getGiaNhap()
        );

        dto.setGiaBan(
                ctsp.getGiaBan()
        );

        dto.setTrangThai(
                ctsp.getTrangThai()
        );

        dto.setTenSanPham(
                ctsp.getSanPham().getTenSanPham()
        );

        dto.setTenMauSac(
                ctsp.getMauSac().getTen()
        );

        dto.setSize(
                ctsp.getKichThuoc().getSize()
        );

        return dto;
    }

}