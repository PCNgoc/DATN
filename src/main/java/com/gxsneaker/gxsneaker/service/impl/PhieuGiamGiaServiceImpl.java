package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.entity.PhieuGiamGia;
import com.gxsneaker.gxsneaker.repository.PhieuGiamGiaRepository;
import com.gxsneaker.gxsneaker.service.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhieuGiamGiaServiceImpl implements PhieuGiamGiaService {

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Override
    public List<PhieuGiamGia> getAllPhieuGiamGia() {
        return phieuGiamGiaRepository.findAll();
    }

    @Override
    public Optional<PhieuGiamGia> getPhieuGiamGiaById(Integer id) {
        return phieuGiamGiaRepository.findById(id);
    }

    @Override
    public Optional<PhieuGiamGia> getPhieuGiamGiaByMa(String maPhieu) {
        return phieuGiamGiaRepository.findAll().stream()
                .filter(p -> p.getMaPhieu() != null && p.getMaPhieu().equalsIgnoreCase(maPhieu))
                .findFirst();
    }

    @Override
    public PhieuGiamGia createPhieuGiamGia(PhieuGiamGia phieuGiamGia) {
        return phieuGiamGiaRepository.save(phieuGiamGia);
    }

    @Override
    public PhieuGiamGia updatePhieuGiamGia(Integer id, PhieuGiamGia details) {
        return phieuGiamGiaRepository.findById(id).map(p -> {
            p.setMaPhieu(details.getMaPhieu());
            p.setTenPhieu(details.getTenPhieu());
            p.setLoaiGiamGia(details.getLoaiGiamGia());
            p.setGiaTriGiam(details.getGiaTriGiam());
            p.setGiaTriDonHangToiThieu(details.getGiaTriDonHangToiThieu());
            p.setGiaTriGiamToiDa(details.getGiaTriGiamToiDa());
            p.setSoLuong(details.getSoLuong());
            p.setNgayBatDau(details.getNgayBatDau());
            p.setNgayKetThuc(details.getNgayKetThuc());
            p.setTrangThai(details.getTrangThai());
            return phieuGiamGiaRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("PhieuGiamGia not found with id " + id));
    }

    @Override
    public void deletePhieuGiamGia(Integer id) {
        phieuGiamGiaRepository.deleteById(id);
    }
}
