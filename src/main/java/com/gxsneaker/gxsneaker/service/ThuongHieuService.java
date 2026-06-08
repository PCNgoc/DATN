package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.ThuongHieuDTO;
import com.gxsneaker.gxsneaker.entity.ThuongHieu;

import java.util.List;

public interface ThuongHieuService {

    List<ThuongHieuDTO> getAll();

    ThuongHieuDTO getById(Long id);

    ThuongHieuDTO create(ThuongHieuDTO dto);

    ThuongHieuDTO update(Long id, ThuongHieuDTO dto);

    void delete(Long id);
}