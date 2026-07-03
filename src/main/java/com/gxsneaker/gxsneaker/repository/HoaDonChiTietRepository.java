package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonChiTietRepository
        extends JpaRepository<HoaDonChiTiet, Long> {

    boolean existsByChiTietSanPham_SanPham_Id(Long id);

}