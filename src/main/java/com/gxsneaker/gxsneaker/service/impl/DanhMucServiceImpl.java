package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.DanhMucDTO;
import com.gxsneaker.gxsneaker.entity.DanhMuc;
import com.gxsneaker.gxsneaker.mapper.DanhMucMapper;
import com.gxsneaker.gxsneaker.repository.DanhMucRepository;
import com.gxsneaker.gxsneaker.service.DanhMucService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DanhMucServiceImpl implements DanhMucService {

    private final DanhMucRepository danhMucRepository;

    // Mapper dùng static -> KHÔNG cần inject
    public DanhMucServiceImpl(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    @Override
    public List<DanhMucDTO> getAll() {
        return danhMucRepository.findAll()
                .stream()
                .map(DanhMucMapper::toDTO)
                .toList();
    }

    @Override
    public DanhMucDTO getById(Long id) {
        return danhMucRepository.findById(id)
                .map(DanhMucMapper::toDTO)
                .orElse(null);
    }

    @Override
    public DanhMucDTO create(DanhMucDTO dto) {
        DanhMuc entity = DanhMucMapper.toEntity(dto);
        // Sinh mã tự động
        String maxMa = danhMucRepository.getMaxMa();

        String maMoi;

        if (maxMa == null) {
            maMoi = "DM001";
        } else {
            int so = Integer.parseInt(maxMa.substring(2));
            maMoi = String.format("DM%03d", so + 1);
        }

        entity.setMa(maMoi);
        entity.setNgayTao(new Date());

        return DanhMucMapper.toDTO(
                danhMucRepository.save(entity)
        );
    }

    @Override
    public DanhMucDTO update(Long id, DanhMucDTO dto) {
        DanhMuc old = danhMucRepository.findById(id).orElse(null);

        if (old == null) return null;
        old.setTen(dto.getTen());
        old.setMoTa(dto.getMoTa());
        old.setThuTuHienThi(dto.getThuTuHienThi());
        old.setTrangThai(dto.getTrangThai());


        return DanhMucMapper.toDTO(
                danhMucRepository.save(old)
        );
    }

    @Override
    public void delete(Long id) {
        danhMucRepository.deleteById(id);
    }
}