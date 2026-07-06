package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.KichThuocDTO;
import com.gxsneaker.gxsneaker.entity.KichThuoc;
import com.gxsneaker.gxsneaker.mapper.KichThuocMapper;
import com.gxsneaker.gxsneaker.repository.KichThuocRepository;
import com.gxsneaker.gxsneaker.service.KichThuocService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KichThuocServiceImpl implements KichThuocService {

    private KichThuocRepository kichThuocRepository;

    public KichThuocServiceImpl(KichThuocRepository kichThuocRepository) {
        this.kichThuocRepository = kichThuocRepository;
    }

    @Override
    public List<KichThuocDTO> getAll() {
        return kichThuocRepository.findAll().stream()
                .map(KichThuocMapper::toDTO)
                .toList();
    }

    @Override
    public KichThuocDTO getById(Long id) {
        return kichThuocRepository.findById(id)
                .map(KichThuocMapper::toDTO)
                .orElse(null);
    }

    @Override
    public KichThuocDTO create(KichThuocDTO dto) {

        KichThuoc kichThuoc = KichThuocMapper.toEntity(dto);
        String maxMa = kichThuocRepository.getMaxMa();

        String maMoi;

        if (maxMa == null) {
            maMoi = "KT35";   // hoặc KT01 nếu muốn bắt đầu từ 1
        } else {
            int so = Integer.parseInt(maxMa.substring(2));
            maMoi = "KT" + (so + 1);
        }

        kichThuoc.setMa(maMoi);

        kichThuocRepository.save(kichThuoc);

        return KichThuocMapper.toDTO(kichThuoc);
    }

    @Override
    public KichThuocDTO update(Long id, KichThuocDTO dto) {

        KichThuoc old = kichThuocRepository.findById(id)
                .orElse(null);

        if (old == null) {
            return null;
        }

        old.setMa(dto.getMa());
        old.setSize(dto.getSize());
        old.setTrangThai(dto.getTrangThai());

        return KichThuocMapper.toDTO(
                kichThuocRepository.save(old)
        );
    }

    @Override
    public void delete(Long id) {
        kichThuocRepository.deleteById(id);
    }
}