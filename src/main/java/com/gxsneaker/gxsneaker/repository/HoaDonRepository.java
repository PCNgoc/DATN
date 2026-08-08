package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.dto.TopSanPhamBanChayDTO;
import com.gxsneaker.gxsneaker.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.gxsneaker.gxsneaker.dto.HoaDonTaiQuayDTO;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
//    List<HoaDon> findByMaHoaDonContaining(String maHoaDon);
//
//    List<HoaDon> findByTrangThai(String trangThai);
    List<HoaDon> findByIdKhachHangOrderByNgayDatHangDesc(Long idKhachHang);
    long countByIdKhachHang(Long idKhachHang);
    boolean existsByIdKhachHangAndTrangThai(Long idKhachHang, String trangThai);

    @Query("SELECT COALESCE(SUM(h.tongTienThanhToan), 0) FROM HoaDon h WHERE h.idKhachHang = :idKhachHang AND h.trangThai = :trangThai")
    BigDecimal sumTongChiTieu(@Param("idKhachHang") Long idKhachHang, @Param("trangThai") String trangThai);

//    List<HoaDon> findByMaHoaDonContainingAndTrangThai(
//            String maHoaDon,
//            String trangThai
//    );
//    List<HoaDon> findByLoaiDonOrderByIdDesc(String loaiDon);
    List<HoaDon> findByLoaiDonOrderByNgayTaoDesc(String loaiDon);

    List<HoaDon> findByLoaiDonAndTrangThaiOrderByNgayTaoDesc(
            String loaiDon,
            String trangThai
    );

    List<HoaDon> findByLoaiDonAndMaHoaDonContainingOrderByNgayTaoDesc(
            String loaiDon,
            String maHoaDon
    );

    List<HoaDon> findByLoaiDonAndMaHoaDonContainingAndTrangThaiOrderByNgayTaoDesc(
            String loaiDon,
            String maHoaDon,
            String trangThai
    );

    List<HoaDon> findByLoaiDon(String loaiDon);

    List<HoaDon> findByLoaiDonAndTrangThai(String loaiDon, String trangThai);

    List<HoaDon> findByLoaiDonAndMaHoaDonContaining(String loaiDon, String maHoaDon);

    List<HoaDon> findByLoaiDonAndMaHoaDonContainingAndTrangThai(
            String loaiDon,
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
SELECT ISNULL(SUM(tong_tien_thanh_toan), 0)
FROM HOA_DON
WHERE trang_thai = 'HOAN_THANH'
AND (:tuNgay IS NULL OR ngay_hoan_thanh >= :tuNgay)
AND (:denNgay IS NULL OR ngay_hoan_thanh < :denNgay)
""", nativeQuery = true)
    BigDecimal getTongDoanhThu(
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );

    // =========================================
// THỐNG KÊ DOANH THU THEO NGÀY
// =========================================
    @Query(value = """
SELECT ISNULL(SUM(tong_tien_thanh_toan),0)
FROM HOA_DON
WHERE trang_thai = 'HOAN_THANH'
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
WHERE trang_thai = 'HOAN_THANH'
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
WHERE trang_thai = 'HOAN_THANH'
AND YEAR(ngay_hoan_thanh) = :year
""", nativeQuery = true)
    BigDecimal getDoanhThuTheoNam(
            @Param("year") int year
    );

    // =========================================
// THỐNG KÊ DOANH THU THEO KHOẢNG THỜI GIAN
// =========================================
    @Query(value = """
SELECT ISNULL(SUM(tong_tien_thanh_toan),0)
FROM HOA_DON
WHERE trang_thai = 'HOAN_THANH'
AND CONVERT(date, ngay_hoan_thanh)
BETWEEN :tuNgay AND :denNgay
""", nativeQuery = true)
    BigDecimal getDoanhThuTheoKhoangThoiGian(
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );
    // =========================================
// THỐNG KÊ TỔNG SỐ ĐƠN HÀNG
// =========================================
    @Query("""
SELECT COUNT(h)
FROM HoaDon h
WHERE (:tuNgay IS NULL OR h.ngayTao >= :tuNgay)
AND (:denNgay IS NULL OR h.ngayTao < :denNgay)
""")
    Long getTongSoDon(
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );


    // =========================================
// THỐNG KÊ SỐ ĐƠN THEO TRẠNG THÁI
// =========================================
    @Query("""
SELECT COUNT(h)
FROM HoaDon h
WHERE h.trangThai = :trangThai
AND (:tuNgay IS NULL OR h.ngayTao >= :tuNgay)
AND (:denNgay IS NULL OR h.ngayTao < :denNgay)
""")
    Long getSoDonTheoTrangThai(
            @Param("trangThai") String trangThai,
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );



    @Query(value = """
WITH THANG AS (
    SELECT 1 AS thang
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
    UNION ALL SELECT 5
    UNION ALL SELECT 6
    UNION ALL SELECT 7
    UNION ALL SELECT 8
    UNION ALL SELECT 9
    UNION ALL SELECT 10
    UNION ALL SELECT 11
    UNION ALL SELECT 12
)
SELECT
    T.thang,
    ISNULL(SUM(HD.tong_tien_thanh_toan),0) AS doanhThu
FROM THANG T
LEFT JOIN HOA_DON HD
    ON MONTH(HD.ngay_hoan_thanh) = T.thang
    AND YEAR(HD.ngay_hoan_thanh) = :year
    AND HD.trang_thai = 'HOAN_THANH'
GROUP BY T.thang
ORDER BY T.thang
""", nativeQuery = true)
    List<Object[]> getDoanhThuTheoThang(@Param("year") Integer year);

    @Query(value = """
SELECT
    CONVERT(date, HD.ngay_hoan_thanh) AS ngay,
    ISNULL(SUM(HD.tong_tien_thanh_toan), 0) AS doanhThu
FROM HOA_DON HD
WHERE HD.trang_thai = 'HOAN_THANH'
AND HD.ngay_hoan_thanh >= :tuNgay
AND HD.ngay_hoan_thanh < :denNgay
GROUP BY CONVERT(date, HD.ngay_hoan_thanh)
ORDER BY CONVERT(date, HD.ngay_hoan_thanh)
""", nativeQuery = true)
    List<Object[]> getDoanhThuTheoNgayTrongKhoang(
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );

    @Query(value = """
SELECT
    trang_thai,
    COUNT(*) AS soLuong
FROM HOA_DON
WHERE trang_thai IS NOT NULL
  AND ngay_dat_hang >= :tuNgay
  AND ngay_dat_hang < :denNgay
GROUP BY trang_thai
ORDER BY trang_thai
""", nativeQuery = true)
    List<Object[]> getThongKeTrangThaiDonHangTheoKhoangNgay(
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );


    @Query(value = """
SELECT TOP 5
    sp.ten_san_pham AS tenSanPham,
    SUM(hdct.so_luong) AS tongSoLuongBan
FROM HOA_DON_CHI_TIET hdct

JOIN HOA_DON hd
    ON hd.id = hdct.id_hoa_don

JOIN CHI_TIET_SAN_PHAM ctsp
    ON ctsp.id = hdct.id_chi_tiet_san_pham

JOIN SAN_PHAM sp
    ON sp.id = ctsp.id_san_pham

WHERE hd.trang_thai = 'HOAN_THANH'
  AND hd.ngay_hoan_thanh >= :tuNgay
  AND hd.ngay_hoan_thanh < :denNgay

GROUP BY sp.ten_san_pham

ORDER BY SUM(hdct.so_luong) DESC
""", nativeQuery = true)
    List<Object[]> getTop5SanPhamBanChayTheoKhoangNgay(
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );


    @Query(value = """
SELECT
    trang_thai,
    COUNT(*) AS soLuong
FROM HOA_DON
GROUP BY trang_thai
""", nativeQuery = true)
    List<Object[]> getThongKeTrangThaiDonHang();


    @Query(value = """
SELECT
    trang_thai,
    COUNT(*) AS soLuong
FROM HOA_DON
WHERE YEAR(ngay_dat_hang) = :year
GROUP BY trang_thai
""", nativeQuery = true)
    List<Object[]> getThongKeTrangThaiDonHang(
            @Param("year") int year
    );


    @Query(value = """
SELECT TOP 5
    sp.ten_san_pham AS tenSanPham,
    SUM(hdct.so_luong) AS tongSoLuongBan
FROM HOA_DON_CHI_TIET hdct
JOIN HOA_DON hd
    ON hd.id = hdct.id_hoa_don
JOIN CHI_TIET_SAN_PHAM ctsp
    ON ctsp.id = hdct.id_chi_tiet_san_pham
JOIN SAN_PHAM sp
    ON sp.id = ctsp.id_san_pham
WHERE hd.trang_thai = 'HOAN_THANH'
AND YEAR(hd.ngay_hoan_thanh) = :year
GROUP BY sp.ten_san_pham
ORDER BY SUM(hdct.so_luong) DESC
""", nativeQuery = true)
    List<Object[]> getTop5SanPhamBanChay(
            @Param("year") Integer year
    );


    //Đơn hàng
    @Query("SELECT COUNT(h) FROM HoaDon h")
    Long countHoaDon();

    Optional<HoaDon> findByPayosOrderCode(Long payosOrderCode);

    List<HoaDon> findByTrangThaiThanhToanAndHanThanhToanBefore(
            String trangThaiThanhToan,
            Date hanThanhToan
    );

    Optional<HoaDon> findByMaHoaDon(String maHoaDon);


    @Query("""
SELECT MAX(h.id)
FROM HoaDon h
""")
    Long getMaxId();


@Query("""
SELECT h
FROM HoaDon h
LEFT JOIN h.khachHang k
WHERE h.loaiDon = :loaiDon

AND (
    :keyword = ''
    OR LOWER(h.maHoaDon) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(k.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR k.soDienThoai LIKE CONCAT('%', :keyword, '%')
)

AND (
    :trangThaiThanhToan = ''
    OR h.trangThaiThanhToan = :trangThaiThanhToan
)

AND (
    :tuNgay IS NULL
    OR h.ngayTao >= :tuNgay
)

AND (
    :denNgay IS NULL
    OR h.ngayTao <= :denNgay
)

ORDER BY h.ngayTao DESC
""")
List<HoaDon> timHoaDonTaiQuay(

        @Param("loaiDon") String loaiDon,

        @Param("keyword") String keyword,

        @Param("trangThaiThanhToan") String trangThaiThanhToan,

        @Param("tuNgay") Date tuNgay,

        @Param("denNgay") Date denNgay

);



    @Query("""
SELECT h
FROM HoaDon h
LEFT JOIN h.khachHang k
WHERE h.loaiDon = :loaiDon

AND (
    :keyword = ''
    OR LOWER(h.maHoaDon) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(k.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR k.soDienThoai LIKE CONCAT('%', :keyword, '%')
)

AND (
    :trangThai = ''
    OR h.trangThai = :trangThai
)

AND (
    :tuNgay = ''
    OR h.ngayTao >= CAST(:tuNgay AS timestamp)
)

AND (
    :denNgay = ''
    OR h.ngayTao <= CAST(:denNgay AS timestamp)
)

ORDER BY h.ngayTao DESC
""")
    Page<HoaDon> timHoaDonOnline(

            @Param("loaiDon") String loaiDon,

            @Param("keyword") String keyword,

            @Param("trangThai") String trangThai,

            @Param("tuNgay") String tuNgay,

            @Param("denNgay") String denNgay,

            Pageable pageable

    );


}
