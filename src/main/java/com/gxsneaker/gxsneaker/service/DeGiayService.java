package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.CoGiayDTO;
import com.gxsneaker.gxsneaker.dto.DeGiayDTO;

import java.util.List;

public interface DeGiayService {
    List<DeGiayDTO> getAll();

    DeGiayDTO getById(Long id);

    DeGiayDTO create(DeGiayDTO dto);

    DeGiayDTO update(Long id, DeGiayDTO dto);

    void delete(Long id);
}
