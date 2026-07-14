package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.ChiTietSanPhamDTO;
import com.gxsneaker.gxsneaker.dto.GenerateVariantDTO;
import com.gxsneaker.gxsneaker.entity.ChiTietSanPham;

import java.util.List;

public interface ChiTietSanPhamService {

    List<ChiTietSanPhamDTO> getAll();

    List<ChiTietSanPhamDTO>
    getBySanPham(Long idSanPham);

    List<ChiTietSanPhamDTO> getBySanPhamUser(Long idSanPham);

    ChiTietSanPhamDTO getById(Long id);

    ChiTietSanPhamDTO create(
            ChiTietSanPhamDTO dto
    );

    ChiTietSanPhamDTO update(
            Long id,
            ChiTietSanPhamDTO dto
    );

    void delete(Long id);
    void generateVariants(GenerateVariantDTO dto);

    List<ChiTietSanPham> getBanTaiQuay();
}