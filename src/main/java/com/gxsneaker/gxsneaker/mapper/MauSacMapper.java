package com.gxsneaker.gxsneaker.mapper;


import com.gxsneaker.gxsneaker.dto.MauSacDTO;

import com.gxsneaker.gxsneaker.entity.MauSac;

public class MauSacMapper {
    public static MauSacDTO toDTO(MauSac dm) {
        MauSacDTO dto = new MauSacDTO();

        dto.setId(dm.getId());
        dto.setMa(dm.getMa());
        dto.setTen(dm.getTen());
        dto.setMaHex(dm.getMaHex());
        dto.setTrangThai(dm.getTrangThai());

        return dto;
    }

    public static MauSac toEntity(MauSacDTO dto) {
        MauSac dm = new MauSac();

        dm.setId(dto.getId());
        dm.setMa(dto.getMa());
        dm.setTen(dto.getTen());
        dm.setMaHex(dto.getMaHex());
        dm.setTrangThai(dto.getTrangThai());

        return dm;
    }
}
