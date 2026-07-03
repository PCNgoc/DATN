package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GioHangChiTietRepository
        extends JpaRepository<GioHangChiTiet, Integer> {

    @Transactional
    void deleteByChiTietSanPham_SanPham_Id(Long id);

}