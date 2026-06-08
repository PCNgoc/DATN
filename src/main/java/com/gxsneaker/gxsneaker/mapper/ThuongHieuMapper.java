package com.gxsneaker.gxsneaker.mapper;

import com.gxsneaker.gxsneaker.dto.ThuongHieuDTO;
import com.gxsneaker.gxsneaker.entity.ThuongHieu;

public class ThuongHieuMapper {

    public static ThuongHieuDTO toDTO(ThuongHieu e) {
        ThuongHieuDTO dto = new ThuongHieuDTO();

        dto.setId(e.getId());
        dto.setMa(e.getMa());
        dto.setTen(e.getTen());
        dto.setLogo(e.getLogo());
        dto.setWebsite(e.getWebsite());
        dto.setMoTa(e.getMoTa());
        dto.setQuocGia(e.getQuocGia());
        dto.setTrangThai(e.getTrangThai());
        dto.setNgayTao(e.getNgayTao());
        dto.setNgayCapNhat(e.getNgayCapNhat());

        return dto;
    }

    public static ThuongHieu toEntity(ThuongHieuDTO dto) {
        ThuongHieu e = new ThuongHieu();

        e.setId(dto.getId());
        e.setMa(dto.getMa());
        e.setTen(dto.getTen());
        e.setLogo(dto.getLogo());
        e.setWebsite(dto.getWebsite());
        e.setMoTa(dto.getMoTa());
        e.setQuocGia(dto.getQuocGia());
        e.setTrangThai(dto.getTrangThai());

        return e;
    }
}