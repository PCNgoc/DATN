package com.gxsneaker.gxsneaker.mapper;


import com.gxsneaker.gxsneaker.dto.DeGiayDTO;
import com.gxsneaker.gxsneaker.entity.DeGiay;

public class DeGiayMapper {
    public static DeGiayDTO toDTO(DeGiay dm) {
        DeGiayDTO dto = new DeGiayDTO();

        dto.setId(dm.getId());
        dto.setMa(dm.getMa());
        dto.setTen(dm.getTen());
        dto.setMoTa(dm.getMoTa());
        dto.setTrangThai(dm.getTrangThai());

        return dto;
    }

    public static DeGiay toEntity(DeGiayDTO dto) {
        DeGiay dm = new DeGiay();

        dm.setId(dto.getId());
        dm.setMa(dto.getMa());
        dm.setTen(dto.getTen());
        dm.setMoTa(dto.getMoTa());
        dm.setTrangThai(dto.getTrangThai());

        return dm;
    }
}
