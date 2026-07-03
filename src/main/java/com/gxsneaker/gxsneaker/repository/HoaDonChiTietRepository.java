package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Long> {
    List<HoaDonChiTiet> findByHoaDonId(Long id);
}