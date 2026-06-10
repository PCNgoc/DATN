package com.gxsneaker.gxsneaker.mapper;

import com.gxsneaker.gxsneaker.dto.KichThuocDTO;
import com.gxsneaker.gxsneaker.entity.KichThuoc;

public class KichThuocMapper {

    public static KichThuocDTO toDTO(KichThuoc kt) {
        KichThuocDTO dto = new KichThuocDTO();

        dto.setId(kt.getId());
        dto.setMa(kt.getMa());
        dto.setSize(kt.getSize());
        dto.setTrangThai(kt.getTrangThai());

        return dto;
    }

    public static KichThuoc toEntity(KichThuocDTO dto) {
        KichThuoc kt = new KichThuoc();

        kt.setId(dto.getId());
        kt.setMa(dto.getMa());
        kt.setSize(dto.getSize());
        kt.setTrangThai(dto.getTrangThai());

        return kt;
    }
}