package com.gxsneaker.gxsneaker.service.impl;


import com.gxsneaker.gxsneaker.dto.CoGiayDTO;
import com.gxsneaker.gxsneaker.entity.CoGiay;
import com.gxsneaker.gxsneaker.entity.XuatXu;
import com.gxsneaker.gxsneaker.mapper.CoGiayMapper;
import com.gxsneaker.gxsneaker.mapper.XuatXuMapper;
import com.gxsneaker.gxsneaker.repository.CoGiayRepository;
import com.gxsneaker.gxsneaker.service.CoGiayService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoGiayServiceImpl implements CoGiayService {
    private CoGiayRepository coGiayRepository;

    public CoGiayServiceImpl(CoGiayRepository coGiayRepository) {
        this.coGiayRepository = coGiayRepository;

    }

    @Override
    public List<CoGiayDTO> getAll() {
        return coGiayRepository.findAll().stream()
                .map(CoGiayMapper::toDTO)
                .toList();
    }

    @Override
    public CoGiayDTO getById(Long id) {
        return coGiayRepository.findById(id).map(CoGiayMapper::toDTO).orElse(null);
    }

    @Override
    public CoGiayDTO create(CoGiayDTO dto) {
        CoGiay coGiay = CoGiayMapper.toEntity(dto);
        coGiayRepository.save(coGiay);
        return CoGiayMapper.toDTO(coGiay);
    }

    @Override
    public CoGiayDTO update(Long id, CoGiayDTO dto) {
        CoGiay old = coGiayRepository.findById(id).orElse(null);

        if (old == null) return null;

        old.setMa(dto.getMa());
        old.setTen(dto.getTen());
        old.setMoTa(dto.getMoTa());
        old.setTrangThai(dto.getTrangThai());


        return CoGiayMapper.toDTO(
                coGiayRepository.save(old)
        );
    }

    @Override
    public void delete(Long id) {
      coGiayRepository.deleteById(id);
    }
}
