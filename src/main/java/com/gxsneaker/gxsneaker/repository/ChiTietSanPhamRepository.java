package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.dto.TopTonKhoDTO;
import com.gxsneaker.gxsneaker.entity.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface ChiTietSanPhamRepository
        extends JpaRepository<ChiTietSanPham, Long> {

    List<ChiTietSanPham> findBySanPhamId(Long idSanPham);

    @Transactional
    void deleteBySanPhamId(Long idSanPham);
    @Query("""
    SELECT MAX(ct.maChiTiet)
    FROM ChiTietSanPham ct
    WHERE ct.maChiTiet LIKE 'CT%'
    """)
    String getMaxMaChiTiet();
    @Query("""
SELECT new com.gxsneaker.gxsneaker.dto.TopTonKhoDTO(
sp.tenSanPham,
SUM(ct.soLuongTon)
)
FROM ChiTietSanPham ct
JOIN ct.sanPham sp
GROUP BY sp.tenSanPham
ORDER BY SUM(ct.soLuongTon) DESC
""")
    List<TopTonKhoDTO> topTonKho(Pageable pageable);

}