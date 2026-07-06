package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.ThuongHieuDTO;
import com.gxsneaker.gxsneaker.entity.ThuongHieu;
import com.gxsneaker.gxsneaker.mapper.ThuongHieuMapper;
import com.gxsneaker.gxsneaker.repository.ThuongHieuRepository;
import com.gxsneaker.gxsneaker.service.ThuongHieuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThuongHieuServiceImpl implements ThuongHieuService {

    private final ThuongHieuRepository repo;

    public ThuongHieuServiceImpl(ThuongHieuRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ThuongHieuDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(ThuongHieuMapper::toDTO)
                .toList();
    }

    @Override
    public ThuongHieuDTO getById(Long id) {
        return repo.findById(id)
                .map(ThuongHieuMapper::toDTO)
                .orElse(null);
    }

    @Override
    public ThuongHieuDTO create(ThuongHieuDTO dto) {
        ThuongHieu e = ThuongHieuMapper.toEntity(dto);
        // Sinh mã tự động
        String maxMa = repo.getMaxMa();

        String maMoi;

        if (maxMa == null) {
            maMoi = "TH001";
        } else {
            int so = Integer.parseInt(maxMa.substring(2));
            maMoi = String.format("TH%03d", so + 1);
        }

        e.setMa(maMoi);
        return ThuongHieuMapper.toDTO(repo.save(e));
    }

    @Override
    public ThuongHieuDTO update(Long id, ThuongHieuDTO dto) {
        ThuongHieu e = repo.findById(id).orElse(null);
        if (e == null) return null;

        e.setMa(dto.getMa());
        e.setTen(dto.getTen());
        e.setLogo(dto.getLogo());
        e.setWebsite(dto.getWebsite());
        e.setMoTa(dto.getMoTa());
        e.setQuocGia(dto.getQuocGia());
        e.setTrangThai(dto.getTrangThai());

        return ThuongHieuMapper.toDTO(repo.save(e));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}