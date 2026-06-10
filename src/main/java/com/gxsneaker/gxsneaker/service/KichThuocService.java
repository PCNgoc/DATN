package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.KichThuocDTO;

import java.util.List;

public interface KichThuocService {

    List<KichThuocDTO> getAll();

    KichThuocDTO getById(Long id);

    KichThuocDTO create(KichThuocDTO dto);

    KichThuocDTO update(Long id, KichThuocDTO dto);

    void delete(Long id);
}