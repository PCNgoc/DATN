package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
    boolean existsByMaSanPham(String maSanPham);
    @Query("""
    SELECT sp
    FROM SanPham sp
    WHERE LOWER(sp.tenSanPham)
    LIKE LOWER(CONCAT('%',:keyword,'%'))
    """)
    List<SanPham> search(String keyword);

    @Query("""
    SELECT sp
    FROM SanPham sp
    ORDER BY sp.ngayTao DESC
    """)
    List<SanPham> findNewestProducts();
    List<SanPham> findTop8ByTrangThaiTrueOrderByNgayTaoDesc();
    List<SanPham> findTop12ByTrangThaiTrue();
}