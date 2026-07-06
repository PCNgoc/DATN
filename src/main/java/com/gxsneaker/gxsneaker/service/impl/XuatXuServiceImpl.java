package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.DanhMucDTO;
import com.gxsneaker.gxsneaker.dto.XuatXuDTO;
import com.gxsneaker.gxsneaker.entity.DanhMuc;
import com.gxsneaker.gxsneaker.entity.XuatXu;
import com.gxsneaker.gxsneaker.mapper.DanhMucMapper;
import com.gxsneaker.gxsneaker.mapper.XuatXuMapper;
import com.gxsneaker.gxsneaker.repository.DanhMucRepository;
import com.gxsneaker.gxsneaker.repository.XuatXuRepository;
import com.gxsneaker.gxsneaker.service.XuatXuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XuatXuServiceImpl implements XuatXuService {

    private final XuatXuRepository xuatXuRepository;

    // Mapper dùng static -> KHÔNG cần inject
    public XuatXuServiceImpl(XuatXuRepository xuatXuRepository) {
        this.xuatXuRepository = xuatXuRepository;
    }
    @Override
    public List<XuatXuDTO> getAll() {
        return xuatXuRepository.findAll()
                .stream().map(XuatXuMapper::toDTO)
                .toList();
    }

    @Override
    public XuatXuDTO getById(Long id) {
        return xuatXuRepository.findById(id).map(XuatXuMapper::toDTO).orElse(null);
    }

    @Override
    public XuatXuDTO create(XuatXuDTO dto) {
        XuatXu xuatXu = XuatXuMapper.toEntity(dto);
        // Sinh mã tự động
        String maxMa = xuatXuRepository.getMaxMa();

        String maMoi;

        if (maxMa == null) {
            maMoi = "XX001";
        } else {
            int so = Integer.parseInt(maxMa.substring(2));
            maMoi = String.format("XX%03d", so + 1);
        }

        xuatXu.setMa(maMoi);
        xuatXuRepository.save(xuatXu);
        return XuatXuMapper.toDTO(xuatXu);
    }

    @Override
    public XuatXuDTO update(Long id, XuatXuDTO dto) {
        XuatXu old = xuatXuRepository.findById(id).orElse(null);

        if (old == null) return null;

        old.setMa(dto.getMa());
        old.setTen(dto.getTen());
        old.setMoTa(dto.getMoTa());
        old.setTrangThai(dto.getTrangThai());


        return XuatXuMapper.toDTO(
                xuatXuRepository.save(old)
        );
    }

    @Override
    public void delete(Long id) {
       xuatXuRepository.deleteById(id);
    }
}
