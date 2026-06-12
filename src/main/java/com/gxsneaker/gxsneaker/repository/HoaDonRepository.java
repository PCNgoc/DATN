package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
    // =========================================
// THỐNG KÊ TỔNG DOANH THU
// =========================================
    @Query(value = """
        SELECT ISNULL(SUM(tong_tien_thanh_toan),0)
        FROM HOA_DON
        WHERE ngay_hoan_thanh IS NOT NULL
        """, nativeQuery = true)
    BigDecimal getTongDoanhThu();


    // =========================================
// THỐNG KÊ DOANH THU THEO NGÀY
// =========================================
    @Query(value = """
        SELECT ISNULL(SUM(tong_tien_thanh_toan),0)
        FROM HOA_DON
        WHERE ngay_hoan_thanh IS NOT NULL
        AND CONVERT(date, ngay_hoan_thanh) = :ngay
        """, nativeQuery = true)
    BigDecimal getDoanhThuTheoNgay(
            @Param("ngay") Date ngay
    );


    // =========================================
// THỐNG KÊ DOANH THU THEO THÁNG
// =========================================
    @Query(value = """
        SELECT ISNULL(SUM(tong_tien_thanh_toan),0)
        FROM HOA_DON
        WHERE ngay_hoan_thanh IS NOT NULL
        AND MONTH(ngay_hoan_thanh) = :month
        AND YEAR(ngay_hoan_thanh) = :year
        """, nativeQuery = true)
    BigDecimal getDoanhThuTheoThang(
            @Param("month") int month,
            @Param("year") int year
    );


    // =========================================
// THỐNG KÊ DOANH THU THEO NĂM
// =========================================
    @Query(value = """
        SELECT ISNULL(SUM(tong_tien_thanh_toan),0)
        FROM HOA_DON
        WHERE ngay_hoan_thanh IS NOT NULL
        AND YEAR(ngay_hoan_thanh) = :year
        """, nativeQuery = true)
    BigDecimal getDoanhThuTheoNam(
            @Param("year") int year
    );


    // =========================================
// THỐNG KÊ TỔNG SỐ ĐƠN HÀNG
// =========================================
    @Query("""
       SELECT COUNT(h)
       FROM HoaDon h
       """)
    Long getTongSoDon();

    // =========================================
// THỐNG KÊ SỐ ĐƠN THEO TRẠNG THÁI
// =========================================
    @Query("""
       SELECT COUNT(h)
       FROM HoaDon h
       WHERE h.trangThai = :trangThai
       """)
    Long getSoDonTheoTrangThai(
            @Param("trangThai") String trangThai
    );

    // =========================================
// THỐNG KÊ DOANH THU THEO KHOẢNG THỜI GIAN
// =========================================
    @Query(value = """
        SELECT ISNULL(SUM(tong_tien_thanh_toan),0)
        FROM HOA_DON
        WHERE ngay_hoan_thanh IS NOT NULL
        AND CONVERT(date, ngay_hoan_thanh)
            BETWEEN :tuNgay AND :denNgay
        """, nativeQuery = true)
    BigDecimal getDoanhThuTheoKhoangThoiGian(
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );
}