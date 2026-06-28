package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.entity.DiaChi;
import com.gxsneaker.gxsneaker.repository.DiaChiRepository;
import com.gxsneaker.gxsneaker.service.DiaChiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiaChiServiceImpl implements DiaChiService {

    @Autowired
    private DiaChiRepository diaChiRepository;

    @Override
    public List<DiaChi> getDiaChiByKhachHangId(Integer khachHangId) {
        return diaChiRepository.findAll().stream()
                .filter(dc -> dc.getKhachHang() != null && dc.getKhachHang().getId().equals(khachHangId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DiaChi> getDiaChiById(Integer id) {
        return diaChiRepository.findById(id);
    }

    @Override
    public DiaChi createDiaChi(DiaChi diaChi) {
        return diaChiRepository.save(diaChi);
    }

    @Override
    public DiaChi updateDiaChi(Integer id, DiaChi diaChiDetails) {
        return diaChiRepository.findById(id).map(diaChi -> {
            diaChi.setHoTenNguoiNhan(diaChiDetails.getHoTenNguoiNhan());
            diaChi.setSoDienThoai(diaChiDetails.getSoDienThoai());
            diaChi.setTinhThanh(diaChiDetails.getTinhThanh());
            diaChi.setQuanHuyen(diaChiDetails.getQuanHuyen());
            diaChi.setPhuongXa(diaChiDetails.getPhuongXa());
            diaChi.setDiaChiChiTiet(diaChiDetails.getDiaChiChiTiet());
            return diaChiRepository.save(diaChi);
        }).orElseThrow(() -> new RuntimeException("DiaChi not found with id " + id));
    }

    @Override
    public void deleteDiaChi(Integer id) {
        diaChiRepository.deleteById(id);
    }
}
