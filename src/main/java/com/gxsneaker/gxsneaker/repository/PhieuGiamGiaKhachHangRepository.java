package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.PhieuGiamGiaKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuGiamGiaKhachHangRepository extends JpaRepository<PhieuGiamGiaKhachHang, Integer> {
    boolean existsByKhachHangIdAndPhieuGiamGiaId(Integer khachHangId, Integer phieuGiamGiaId);
    boolean existsByKhachHangIdAndPhieuGiamGiaIdAndDaSuDungFalse(Integer khachHangId, Integer phieuGiamGiaId);
    java.util.Optional<PhieuGiamGiaKhachHang> findByKhachHangIdAndPhieuGiamGiaId(Integer khachHangId, Integer phieuGiamGiaId);
}
