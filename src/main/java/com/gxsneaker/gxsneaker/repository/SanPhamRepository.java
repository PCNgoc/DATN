package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
    boolean existsByMaSanPham(String maSanPham);
    @Query("SELECT COALESCE(MAX(sp.id),0) FROM SanPham sp")
    Long getMaxId();
    @Query("""
    SELECT MAX(sp.maSanPham)
    FROM SanPham sp
    WHERE sp.maSanPham LIKE 'SP%'
    """)
    String getMaxMaSanPham();
    @Query("""
SELECT sp
FROM SanPham sp
WHERE sp.trangThai = true
AND LOWER(sp.tenSanPham)
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

    List<SanPham> findByTrangThaiTrue();


    @Query("""
SELECT sp
FROM SanPham sp
WHERE sp.trangThai = true

AND (
    :keyword IS NULL
    OR :keyword = ''
    OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))
)

AND (
    :brandId IS NULL
    OR sp.thuongHieu.id = :brandId
)

AND (
    :categoryId IS NULL
    OR sp.danhMuc.id = :categoryId
)

AND (
    :xuatXuId IS NULL
    OR sp.xuatXu.id = :xuatXuId
)

AND (
    :chatLieuId IS NULL
    OR sp.chatLieu.id = :chatLieuId
)

AND (
    :coGiayId IS NULL
    OR sp.coGiay.id = :coGiayId
)

AND (
    :deGiayId IS NULL
    OR sp.deGiay.id = :deGiayId
)

AND (
    :gioiTinh IS NULL
    OR :gioiTinh = ''
    OR sp.gioiTinh = :gioiTinh
)

ORDER BY sp.ngayTao DESC
""")
    List<SanPham> filter(
            String keyword,
            Long brandId,
            Long categoryId,
            Long xuatXuId,
            Long chatLieuId,
            Long coGiayId,
            Long deGiayId,
            String gioiTinh
    );
}