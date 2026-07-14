package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.dto.HoaDonChiTietBanHangDTO;
import com.gxsneaker.gxsneaker.entity.HoaDonChiTiet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Long> {

    List<HoaDonChiTiet> findByHoaDonId(Long id);


    boolean existsByChiTietSanPham_SanPham_Id(Long id);

    HoaDonChiTiet findByHoaDon_IdAndChiTietSanPham_Id(
            Long idHoaDon,
            Long idChiTietSanPham
    );

    @Query("""
SELECT new com.gxsneaker.gxsneaker.dto.HoaDonChiTietBanHangDTO(
    hdct.id,
    ctsp.id,
    sp.tenSanPham,
    ms.ten,
    kt.size,
    hdct.soLuong,
    hdct.donGia,
    hdct.thanhTien,
    sp.anhDaiDien
)
FROM HoaDonChiTiet hdct
JOIN hdct.chiTietSanPham ctsp
JOIN ctsp.sanPham sp
JOIN ctsp.mauSac ms
JOIN ctsp.kichThuoc kt
WHERE hdct.hoaDon.id = :hoaDonId
""")
    List<HoaDonChiTietBanHangDTO> getBanHangByHoaDon(Long hoaDonId);
    @Transactional
    void deleteById(Long id);

}