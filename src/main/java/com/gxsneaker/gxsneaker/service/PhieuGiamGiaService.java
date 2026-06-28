package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.entity.PhieuGiamGia;
import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaService {
    List<PhieuGiamGia> getAllPhieuGiamGia();
    Optional<PhieuGiamGia> getPhieuGiamGiaById(Integer id);
    Optional<PhieuGiamGia> getPhieuGiamGiaByMa(String maPhieu);
    PhieuGiamGia createPhieuGiamGia(PhieuGiamGia phieuGiamGia);
    PhieuGiamGia updatePhieuGiamGia(Integer id, PhieuGiamGia phieuGiamGia);
    void deletePhieuGiamGia(Integer id);
}
