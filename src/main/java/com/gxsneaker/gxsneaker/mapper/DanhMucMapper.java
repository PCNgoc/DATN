package com.gxsneaker.gxsneaker.mapper;

import com.gxsneaker.gxsneaker.dto.DanhMucDTO;
import com.gxsneaker.gxsneaker.entity.DanhMuc;

public class DanhMucMapper {

    public static DanhMucDTO toDTO(DanhMuc dm) {
        DanhMucDTO dto = new DanhMucDTO();

        dto.setId(dm.getId());
        dto.setMa(dm.getMa());
        dto.setTen(dm.getTen());
        dto.setMoTa(dm.getMoTa());
        dto.setThuTuHienThi(dm.getThuTuHienThi());
        dto.setTrangThai(dm.getTrangThai());
        dto.setNgayTao(dm.getNgayTao());

        return dto;
    }

    public static DanhMuc toEntity(DanhMucDTO dto) {
        DanhMuc dm = new DanhMuc();

        dm.setId(dto.getId());
        dm.setMa(dto.getMa());
        dm.setTen(dto.getTen());
        dm.setMoTa(dto.getMoTa());
        dm.setThuTuHienThi(dto.getThuTuHienThi());
        dm.setTrangThai(dto.getTrangThai());
        dm.setNgayTao(dto.getNgayTao());

        return dm;
    }
}