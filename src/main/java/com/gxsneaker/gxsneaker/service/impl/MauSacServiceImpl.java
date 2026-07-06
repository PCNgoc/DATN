package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.MauSacDTO;
import com.gxsneaker.gxsneaker.entity.DeGiay;
import com.gxsneaker.gxsneaker.entity.MauSac;
import com.gxsneaker.gxsneaker.mapper.DeGiayMapper;
import com.gxsneaker.gxsneaker.mapper.MauSacMapper;
import com.gxsneaker.gxsneaker.repository.DeGiayRepository;
import com.gxsneaker.gxsneaker.repository.MauSacRepository;
import com.gxsneaker.gxsneaker.service.MauSacService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MauSacServiceImpl implements MauSacService {
    private MauSacRepository mauSacRepository;

    public MauSacServiceImpl(MauSacRepository mauSacRepository) {
        this.mauSacRepository = mauSacRepository;
    }

    @Override
    public List<MauSacDTO> getAll() {
        return mauSacRepository.findAll().stream().map(MauSacMapper::toDTO).toList() ;
    }

    @Override
    public MauSacDTO getById(Long id) {
        return mauSacRepository.findById(id).map(MauSacMapper::toDTO).orElse(null);
    }

    @Override
    public MauSacDTO create(MauSacDTO dto) {
        MauSac mauSac = MauSacMapper.toEntity(dto);
        // Sinh mã tự động
        String maxMa = mauSacRepository.getMaxMa();

        String maMoi;

        if (maxMa == null) {
            maMoi = "MS001";
        } else {
            int so = Integer.parseInt(maxMa.substring(2));
            maMoi = String.format("MS%03d", so + 1);
        }

        mauSac.setMa(maMoi);
        mauSacRepository.save(mauSac);
        return MauSacMapper.toDTO(mauSac);
    }

    @Override
    public MauSacDTO update(Long id, MauSacDTO dto) {
        MauSac old = mauSacRepository.findById(id).orElse(null);

        if (old == null) return null;

        old.setMa(dto.getMa());
        old.setTen(dto.getTen());
        old.setMaHex(dto.getMaHex());
        old.setTrangThai(dto.getTrangThai());


        return MauSacMapper.toDTO(
                mauSacRepository.save(old)
        );
    }

    @Override
    public void delete(Long id) {
      mauSacRepository.deleteById(id);
    }
}
