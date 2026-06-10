package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.CoGiayDTO;

import java.util.List;

public interface CoGiayService {
    List<CoGiayDTO> getAll();

    CoGiayDTO getById(Long id);

    CoGiayDTO create(CoGiayDTO dto);

    CoGiayDTO update(Long id, CoGiayDTO dto);

    void delete(Long id);
}
