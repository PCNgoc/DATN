package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.LichSuTrangThaiHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuTrangThaiHoaDonRepository extends JpaRepository<LichSuTrangThaiHoaDon, Long> {

    // ==========================================
// CHỨC NĂNG: LẤY LỊCH SỬ TRẠNG THÁI
// THEO ID HÓA ĐƠN
// ==========================================
    List<LichSuTrangThaiHoaDon>
    findByIdHoaDonOrderByThoiGianAsc(
            Long idHoaDon
    );
}