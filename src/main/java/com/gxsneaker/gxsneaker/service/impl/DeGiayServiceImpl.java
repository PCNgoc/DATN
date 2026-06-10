package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.DeGiayDTO;
import com.gxsneaker.gxsneaker.entity.CoGiay;
import com.gxsneaker.gxsneaker.entity.DeGiay;
import com.gxsneaker.gxsneaker.mapper.CoGiayMapper;
import com.gxsneaker.gxsneaker.mapper.DeGiayMapper;
import com.gxsneaker.gxsneaker.repository.CoGiayRepository;
import com.gxsneaker.gxsneaker.repository.DeGiayRepository;
import com.gxsneaker.gxsneaker.service.DeGiayService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeGiayServiceImpl implements DeGiayService {
    private DeGiayRepository deGiayRepository;

    public DeGiayServiceImpl(DeGiayRepository deGiayRepository) {
        this.deGiayRepository = deGiayRepository;
    }

    @Override
    public List<DeGiayDTO> getAll() {
        return deGiayRepository.findAll().stream()
                .map(DeGiayMapper::toDTO)
                .toList();
    }

    @Override
    public DeGiayDTO getById(Long id) {
        return deGiayRepository.findById(id).map(DeGiayMapper::toDTO).orElse(null);
    }

    @Override
    public DeGiayDTO create(DeGiayDTO dto) {
        DeGiay  deGiay = DeGiayMapper.toEntity(dto);
        deGiayRepository.save(deGiay);
        return DeGiayMapper.toDTO(deGiay);
    }

    @Override
    public DeGiayDTO update(Long id, DeGiayDTO dto) {
        DeGiay old = deGiayRepository.findById(id).orElse(null);

        if (old == null) return null;

        old.setMa(dto.getMa());
        old.setTen(dto.getTen());
        old.setMoTa(dto.getMoTa());
        old.setTrangThai(dto.getTrangThai());


        return DeGiayMapper.toDTO(
                deGiayRepository.save(old)
        );
    }

    @Override
    public void delete(Long id) {
     deGiayRepository.deleteById(id);
    }
}
