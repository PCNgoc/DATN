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

    private void validatePhieuGiamGia(PhieuGiamGia p, Integer excludeId) {
        if (p.getMaPhieu() == null || p.getMaPhieu().trim().isEmpty()) {
            throw new RuntimeException("Mã phiếu giảm giá không được để trống!");
        }
        String ma = p.getMaPhieu().trim().toUpperCase();
        p.setMaPhieu(ma);

        if (p.getTenPhieu() == null || p.getTenPhieu().trim().isEmpty()) {
            throw new RuntimeException("Tên phiếu giảm giá không được để trống!");
        }

        // Check uniqueness of maPhieu
        Optional<PhieuGiamGia> existing = phieuGiamGiaRepository.findByMaPhieu(ma);
        if (existing.isPresent()) {
            if (excludeId == null || !existing.get().getId().equals(excludeId)) {
                throw new RuntimeException("Mã phiếu giảm giá này đã tồn tại trong hệ thống!");
            }
        }

        if (p.getGiaTriGiam() == null || p.getGiaTriGiam().doubleValue() <= 0) {
            throw new RuntimeException("Giá trị giảm phải lớn hơn 0!");
        }

        if (p.getLoaiGiamGia() != null && p.getLoaiGiamGia()) { // percentage
            if (p.getGiaTriGiam().doubleValue() > 100) {
                throw new RuntimeException("Giá trị giảm theo phần trăm không được vượt quá 100%!");
            }
        }

        if (p.getGiaTriDonHangToiThieu() == null || p.getGiaTriDonHangToiThieu().doubleValue() < 0) {
            throw new RuntimeException("Giá trị đơn hàng tối thiểu không được nhỏ hơn 0!");
        }

        if (p.getLoaiGiamGia() != null && p.getLoaiGiamGia()) {
            if (p.getGiaTriGiamToiDa() == null || p.getGiaTriGiamToiDa().doubleValue() < 0) {
                throw new RuntimeException("Giá trị giảm tối đa không được nhỏ hơn 0!");
            }
        } else {
            p.setGiaTriGiamToiDa(java.math.BigDecimal.ZERO);
        }

        if (p.getSoLuong() == null || p.getSoLuong() < 0) {
            throw new RuntimeException("Số lượng phát hành không được nhỏ hơn 0!");
        }

        if (p.getNgayBatDau() == null) {
            throw new RuntimeException("Ngày bắt đầu không được để trống!");
        }

        if (p.getNgayKetThuc() == null) {
            throw new RuntimeException("Ngày kết thúc không được để trống!");
        }

        if (p.getNgayKetThuc().before(p.getNgayBatDau())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu!");
        }
    }

    @Override
    public PhieuGiamGia createPhieuGiamGia(PhieuGiamGia phieuGiamGia) {
        validatePhieuGiamGia(phieuGiamGia, null);
        return phieuGiamGiaRepository.save(phieuGiamGia);
    }

    @Override
    public PhieuGiamGia updatePhieuGiamGia(Integer id, PhieuGiamGia details) {
        validatePhieuGiamGia(details, id);
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
