package com.gxsneaker.gxsneaker.mapper;

import com.gxsneaker.gxsneaker.dto.DanhMucDTO;
import com.gxsneaker.gxsneaker.dto.XuatXuDTO;
import com.gxsneaker.gxsneaker.entity.DanhMuc;
import com.gxsneaker.gxsneaker.entity.XuatXu;

public class XuatXuMapper {

    public static XuatXuDTO toDTO(XuatXu dm) {
        XuatXuDTO dto = new XuatXuDTO();

        dto.setId(dm.getId());
        dto.setMa(dm.getMa());
        dto.setTen(dm.getTen());
        dto.setMoTa(dm.getMoTa());
        dto.setTrangThai(dm.getTrangThai());

        return dto;
    }

    public static XuatXu toEntity(XuatXuDTO dto) {
        XuatXu dm = new XuatXu();

        dm.setId(dto.getId());
        dm.setMa(dto.getMa());
        dm.setTen(dto.getTen());
        dm.setMoTa(dto.getMoTa());
        dm.setTrangThai(dto.getTrangThai());

        return dm;
    }
}
