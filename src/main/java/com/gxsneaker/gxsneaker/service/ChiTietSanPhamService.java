package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.ChiTietSanPhamDTO;

import java.util.List;

public interface ChiTietSanPhamService {

    List<ChiTietSanPhamDTO> getAll();

    List<ChiTietSanPhamDTO>
    getBySanPham(Long idSanPham);

    ChiTietSanPhamDTO getById(Long id);

    ChiTietSanPhamDTO create(
            ChiTietSanPhamDTO dto
    );

    ChiTietSanPhamDTO update(
            Long id,
            ChiTietSanPhamDTO dto
    );

    void delete(Long id);
}