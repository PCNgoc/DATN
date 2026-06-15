package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
    boolean existsByMaSanPham(String maSanPham);

}