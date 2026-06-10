package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.MauSacDTO;

import java.util.List;

public interface MauSacService {
    List<MauSacDTO> getAll();

    MauSacDTO getById(Long id);

    MauSacDTO create(MauSacDTO dto);

    MauSacDTO update(Long id, MauSacDTO dto);

    void delete(Long id);
}
