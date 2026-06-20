package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.SanPhamDTO;

import java.util.List;

public interface SanPhamService {

    List<SanPhamDTO> getAll();

    SanPhamDTO getById(Long id);

    SanPhamDTO create(SanPhamDTO dto);

    SanPhamDTO update(Long id, SanPhamDTO dto);

    void delete(Long id);
    List<SanPhamDTO> search(String keyword);
}