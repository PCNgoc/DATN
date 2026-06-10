package com.gxsneaker.gxsneaker.mapper;


import com.gxsneaker.gxsneaker.dto.CoGiayDTO;
import com.gxsneaker.gxsneaker.entity.CoGiay;

public class CoGiayMapper {
    public static CoGiayDTO toDTO(CoGiay dm) {
        CoGiayDTO dto = new CoGiayDTO();

        dto.setId(dm.getId());
        dto.setMa(dm.getMa());
        dto.setTen(dm.getTen());
        dto.setMoTa(dm.getMoTa());
        dto.setTrangThai(dm.getTrangThai());

        return dto;
    }

    public static CoGiay toEntity(CoGiayDTO dto) {
        CoGiay dm = new CoGiay();

        dm.setId(dto.getId());
        dm.setMa(dto.getMa());
        dm.setTen(dto.getTen());
        dm.setMoTa(dto.getMoTa());
        dm.setTrangThai(dto.getTrangThai());

        return dm;
    }
}
