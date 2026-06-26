package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.entity.KhachHang;
import com.gxsneaker.gxsneaker.repository.KhachHangRepository;
import com.gxsneaker.gxsneaker.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Override
    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    @Override
    public Optional<KhachHang> getKhachHangById(Integer id) {
        return khachHangRepository.findById(id);
    }

    @Override
    public KhachHang createKhachHang(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    @Override
    public KhachHang updateKhachHang(Integer id, KhachHang khachHangDetails) {
        return khachHangRepository.findById(id).map(khachHang -> {
            khachHang.setMaKhachHang(khachHangDetails.getMaKhachHang());
            khachHang.setHoTen(khachHangDetails.getHoTen());
            khachHang.setEmail(khachHangDetails.getEmail());
            khachHang.setSoDienThoai(khachHangDetails.getSoDienThoai());
            khachHang.setGioiTinh(khachHangDetails.getGioiTinh());
            khachHang.setNgaySinh(khachHangDetails.getNgaySinh());
            khachHang.setTrangThai(khachHangDetails.getTrangThai());
            return khachHangRepository.save(khachHang);
        }).orElseThrow(() -> new RuntimeException("KhachHang not found with id " + id));
    }

    @Override
    public void deleteKhachHang(Integer id) {
        khachHangRepository.deleteById(id);
    }
}
