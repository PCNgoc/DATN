package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    Optional<PhieuGiamGia> findByMaPhieu(String maPhieu);

    Optional<PhieuGiamGia> findByMaPhieuIgnoreCase(String maPhieu);

    @Modifying
    @Query("UPDATE PhieuGiamGia p SET p.soLuong = p.soLuong - 1 WHERE p.id = :id AND p.soLuong > 0")
    int decrementSoLuong(@Param("id") Integer id);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("UPDATE PhieuGiamGia p SET p.trangThai = false WHERE p.ngayKetThuc < CURRENT_TIMESTAMP AND p.trangThai = true")
    int updateTrangThaiHetHan();
}