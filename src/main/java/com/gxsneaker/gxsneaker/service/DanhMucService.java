package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.DanhMucDTO;
import com.gxsneaker.gxsneaker.entity.DanhMuc;

import java.util.List;

public interface DanhMucService {

    List<DanhMucDTO> getAll();

    DanhMucDTO getById(Long id);

    DanhMucDTO create(DanhMucDTO dto);

    DanhMucDTO update(Long id, DanhMucDTO dto);

    void delete(Long id);
}