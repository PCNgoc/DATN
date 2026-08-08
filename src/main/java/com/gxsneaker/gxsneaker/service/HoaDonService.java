package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.*;
import com.gxsneaker.gxsneaker.entity.HoaDon;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import com.gxsneaker.gxsneaker.dto.ThemSanPhamRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface HoaDonService {

        List<DoanhThuTheoThangDTO> getDoanhThuTheoThang(int year);
    List<DoanhThuTheoNgayResponse> getDoanhThuTheoNgayTrongKhoang(
            Date tuNgay,
            Date denNgay
    );
    @Data
    public class DoanhThuTheoNgayResponse {

        private Date ngay;
        private BigDecimal doanhThu;

        public DoanhThuTheoNgayResponse(
                Date ngay,
                BigDecimal doanhThu
        ) {
            this.ngay = ngay;
            this.doanhThu = doanhThu;
        }
    }

        List<TrangThaiDonHangDTO> getThongKeTrangThaiDonHang(int year);

    // Đặt haàng
    HoaDon datHang(DatHangRequestDTO request);
    //Chi tiết đơn
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByCustomer(Long customerId);

    List<TopSanPhamBanChayDTO> getTop5SanPhamBanChay(int year);

    HoaDon capNhatTrangThai(Long idHoaDon, UpdateTrangThaiRequest request);

    void huyDon(Long id, String ghiChu, String nguoiThucHien);
    List<TopTonKhoDTO> getTop5TonKho();

    void xacNhanThanhToanVnpay(Long hoaDonId, String maGiaoDich);

    ResponseEntity<byte[]> exportPdf(Long id);

    HoaDon taoHoaDonCho(String authHeader);
    List<HoaDon> getHoaDonCho();

    void themSanPham(Long hoaDonId, ThemSanPhamRequest request);
    void capNhatSoLuong(Long hoaDonChiTietId,Integer soLuong);
    void doiKhachHang(Long hoaDonId, Long khachHangId);
    void xoaSanPham(Long hoaDonChiTietId);
    void thanhToan(Long hoaDonId, ThanhToanRequest request);
    Page<HoaDon> getHoaDonOnline(

            String keyword,

            String trangThai,

            String tuNgay,

            String denNgay,

            Integer page,

            Integer size

    );
    List<HoaDonTaiQuayDTO> getHoaDonTaiQuay(
            String keyword,
            String trangThaiThanhToan,
            String tuNgay,
            String denNgay
    );
    void apDungVoucherTaiQuay(Long hoaDonId, String maVoucher);
    void xoaVoucherTaiQuay(Long hoaDonId);

}