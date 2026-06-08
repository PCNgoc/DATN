package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Date;
import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    List<HoaDon> findByMaHoaDonContaining(String maHoaDon);

    List<HoaDon> findByTrangThai(String trangThai);

    List<HoaDon> findByMaHoaDonContainingAndTrangThai(
            String maHoaDon,
            String trangThai
    );

    // ==========================================
    // CHỨC NĂNG: LỌC HÓA ĐƠN THEO NGÀY ĐẶT HÀNG
    // NGHIỆP VỤ:
    // Lấy danh sách hóa đơn trong khoảng thời gian
    // ==========================================
    List<HoaDon> findByNgayDatHangBetween(
            Date from,
            Date to
    );
}