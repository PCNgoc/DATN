package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.TopSanPhamBanChayDTO;
import com.gxsneaker.gxsneaker.dto.UpdateTrangThaiRequest;
import com.gxsneaker.gxsneaker.entity.HoaDon;
import com.gxsneaker.gxsneaker.entity.LichSuTrangThaiHoaDon;
import com.gxsneaker.gxsneaker.repository.HoaDonRepository;
import com.gxsneaker.gxsneaker.repository.LichSuTrangThaiHoaDonRepository;
import com.gxsneaker.gxsneaker.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import com.gxsneaker.gxsneaker.dto.ThongKeDashboardResponse;

@RestController
@RequestMapping("/api/hoa-don")
@CrossOrigin("*")
public class HoaDonController {

    @Autowired
    private HoaDonRepository repository;
    @Autowired
    private LichSuTrangThaiHoaDonRepository lichSuRepository;
    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping
    public List<HoaDon> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public HoaDon getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public HoaDon create(@RequestBody HoaDon hoaDon) {
        return repository.save(hoaDon);
    }

    @PutMapping("/{id}")
    public HoaDon update(@PathVariable Long id,
                         @RequestBody HoaDon hoaDon) {

        hoaDon.setId(id);
        return repository.save(hoaDon);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    //Chức năng tìm kiếm theo maHoaDon, trang thái
    @GetMapping("/search")
    public List<HoaDon> search(
            @RequestParam(required = false) String maHoaDon,
            @RequestParam(required = false) String trangThai
    ) {

        if ((maHoaDon == null || maHoaDon.isBlank())
                && (trangThai == null || trangThai.isBlank())) {

            return repository.findAll();
        }

        if (trangThai == null || trangThai.isBlank()) {
            return repository.findByMaHoaDonContaining(maHoaDon);
        }

        if (maHoaDon == null || maHoaDon.isBlank()) {
            return repository.findByTrangThai(trangThai);
        }

        return repository.findByMaHoaDonContainingAndTrangThai(
                maHoaDon,
                trangThai
        );
    }

    // ==========================================
// CHỨC NĂNG: PHÂN TRANG DANH SÁCH HÓA ĐƠN
// API: GET /api/hoa-don/page
// MÔ TẢ:
// - Hiển thị danh sách hóa đơn theo trang
// - page bắt đầu từ 0
// - size là số bản ghi trên mỗi trang
// ==========================================
    @GetMapping("/page")
    public Page<HoaDon> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return repository.findAll(PageRequest.of(page, size));
    }

    // ==========================================
// CHỨC NĂNG: LỌC HÓA ĐƠN THEO NGÀY
// API:
// GET /api/hoa-don/filter-date
// Ví dụ:
// /api/hoa-don/filter-date?from=2025-01-01&to=2025-12-31
// NGHIỆP VỤ:
// Hiển thị các hóa đơn được tạo trong
// khoảng thời gian người dùng lựa chọn
// ==========================================
    @GetMapping("/filter-date")
    public List<HoaDon> filterDate(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date from,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date to
    ) {

        return repository.findByNgayDatHangBetween(
                from,
                to
        );
    }


    // ==========================================
// CHỨC NĂNG: CẬP NHẬT TRẠNG THÁI HÓA ĐƠN
// API: PUT /api/hoa-don/{id}/status
//
// MÔ TẢ:
// 1. Tìm hóa đơn theo ID
// 2. Kiểm tra hóa đơn tồn tại
// 3. Kiểm tra trạng thái mới khác trạng thái hiện tại
// 4. Cập nhật trạng thái mới
// 5. Lưu lịch sử thay đổi trạng thái
// ==========================================

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateTrangThaiRequest request
    ) {

        Optional<HoaDon> optionalHoaDon =
                repository.findById(id);

        if (optionalHoaDon.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Không tìm thấy hóa đơn");
        }

        HoaDon hoaDon = optionalHoaDon.get();

        String trangThaiCu = hoaDon.getTrangThai();

        // Kiểm tra trạng thái mới trùng trạng thái cũ
        if (trangThaiCu.equalsIgnoreCase(
                request.getTrangThaiMoi()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body("Trạng thái mới phải khác trạng thái hiện tại");
        }

        // Cập nhật trạng thái hóa đơn
        hoaDon.setTrangThai(
                request.getTrangThaiMoi()
        );

        repository.save(hoaDon);

        // Lưu lịch sử thay đổi trạng thái
        LichSuTrangThaiHoaDon lichSu =
                new LichSuTrangThaiHoaDon();

        lichSu.setIdHoaDon(id);

        lichSu.setTrangThaiCu(trangThaiCu);

        lichSu.setTrangThaiMoi(
                request.getTrangThaiMoi()
        );

        lichSu.setNguoiThucHien(
                request.getNguoiThucHien()
        );

        lichSu.setThoiGian(new Date());

        lichSu.setGhiChu(
                request.getGhiChu()
        );

        lichSuRepository.save(lichSu);

        return ResponseEntity.ok(
                "Cập nhật trạng thái thành công"
        );
    }

    // ==========================================
// CHỨC NĂNG: XEM LỊCH SỬ TRẠNG THÁI
// API:
// GET /api/hoa-don/{id}/history
//
// MÔ TẢ:
// Lấy toàn bộ lịch sử thay đổi trạng thái
// của một hóa đơn
// ==========================================
    @GetMapping("/{id}/history")
    public List<LichSuTrangThaiHoaDon> getHistory(
            @PathVariable Long id
    ) {

        return lichSuRepository
                .findByIdHoaDonOrderByThoiGianAsc(id);
    }


    // =====================================================
// THỐNG KÊ TỔNG DOANH THU
// =====================================================
    @GetMapping("/thong-ke/tong-doanh-thu")
    public ResponseEntity<BigDecimal> getTongDoanhThu() {

        return ResponseEntity.ok(
                repository.getTongDoanhThu()
        );
    }

    // =====================================================
// THỐNG KÊ DOANH THU THEO THÁNG
// =====================================================
    @GetMapping("/thong-ke/doanh-thu-thang")
    public ResponseEntity<BigDecimal> getDoanhThuTheoThang(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                repository.getDoanhThuTheoThang(month, year)
        );
    }

    // =====================================================
// THỐNG KÊ DOANH THU THEO NĂM
// =====================================================
    @GetMapping("/thong-ke/doanh-thu-nam")
    public ResponseEntity<BigDecimal> getDoanhThuTheoNam(
            @RequestParam int year) {

        return ResponseEntity.ok(
                repository.getDoanhThuTheoNam(year)
        );
    }

    // =====================================================
// THỐNG KÊ DOANH THU THEO NGÀY
// =====================================================
    @GetMapping("/thong-ke/doanh-thu-ngay")
    public ResponseEntity<BigDecimal> getDoanhThuTheoNgay(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date ngay) {

        return ResponseEntity.ok(
                repository.getDoanhThuTheoNgay(ngay)
        );
    }

    // =====================================================
// THỐNG KÊ TỔNG SỐ ĐƠN HÀNG
// =====================================================
    @GetMapping("/thong-ke/tong-so-don")
    public ResponseEntity<Long> getTongSoDon() {

        return ResponseEntity.ok(
                repository.getTongSoDon()
        );
    }

    // =====================================================
// THỐNG KÊ SỐ ĐƠN THEO TRẠNG THÁI
// =====================================================
    @GetMapping("/thong-ke/so-don-theo-trang-thai")
    public ResponseEntity<Long> getSoDonTheoTrangThai(
            @RequestParam String trangThai) {

        return ResponseEntity.ok(
                repository.getSoDonTheoTrangThai(trangThai)
        );
    }


    // =====================================================
// DASHBOARD THỐNG KÊ TỔNG QUAN
// =====================================================
    @GetMapping("/thong-ke/dashboard")
    public ResponseEntity<ThongKeDashboardResponse> getDashboard() {

        ThongKeDashboardResponse response =
                new ThongKeDashboardResponse();

        response.setTongSoDon(
                repository.getTongSoDon()
        );

        response.setTongDoanhThu(
                repository.getTongDoanhThu()
        );

        response.setSoDonChoXacNhan(
                repository.getSoDonTheoTrangThai(
                        "CHO_XAC_NHAN"
                )
        );

        response.setSoDonDaXacNhan(
                repository.getSoDonTheoTrangThai(
                        "DA_XAC_NHAN"
                )
        );

        response.setSoDonDangGiao(
                repository.getSoDonTheoTrangThai(
                        "DANG_GIAO"
                )
        );

        response.setSoDonHoanThanh(
                repository.getSoDonTheoTrangThai(
                        "HOAN_THANH"
                )
        );

        response.setSoDonDaHuy(
                repository.getSoDonTheoTrangThai(
                        "DA_HUY"
                )
        );

        return ResponseEntity.ok(response);
    }

    // =====================================================
// THỐNG KÊ DOANH THU THEO KHOẢNG THỜI GIAN
// =====================================================
    @GetMapping("/thong-ke/doanh-thu-khoang-thoi-gian")
    public ResponseEntity<BigDecimal> getDoanhThuTheoKhoangThoiGian(

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date tuNgay,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date denNgay
    ) {

        return ResponseEntity.ok(
                repository.getDoanhThuTheoKhoangThoiGian(
                        tuNgay,
                        denNgay
                )
        );
    }


    // =====================================================
// THỐNG KÊ BIỂU Ò THEO THÁNG
// =====================================================
    @GetMapping("/thong-ke/bieu-do-doanh-thu-thang")
    public ResponseEntity<?> getDoanhThuTheoThang(
            @RequestParam int year) {

        return ResponseEntity.ok(
                hoaDonService.getDoanhThuTheoThang(year)
        );
    }

    // =====================================================
// BIỂU ĐỒ DOANH THU THEO THÁNG
// =====================================================
//    @GetMapping("/thong-ke/bieu-do-doanh-thu-thang")
//    public ResponseEntity<?> getBieuDoDoanhThuTheoThang(
//            @RequestParam int year
//    ) {
//
//        return ResponseEntity.ok(
//                hoaDonService.getDoanhThuTheoThang(year)
//        );
//    }

    @GetMapping("/thong-ke/trang-thai-don-hang")
    public ResponseEntity<?> getThongKeTrangThaiDonHang(
            @RequestParam int year
    ) {

        return ResponseEntity.ok(
                hoaDonService.getThongKeTrangThaiDonHang(year)
        );
    }

    @GetMapping("/top-5-san-pham")
    public ResponseEntity<List<TopSanPhamBanChayDTO>>
    getTop5SanPham(@RequestParam Integer year) {

        return ResponseEntity.ok(
                hoaDonService.getTop5SanPhamBanChay(year)
        );
    }


}
