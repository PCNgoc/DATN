package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.*;
import com.gxsneaker.gxsneaker.entity.HoaDon;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import com.gxsneaker.gxsneaker.dto.ThemSanPhamRequest;
import java.util.List;

public interface HoaDonService {

        List<DoanhThuTheoThangDTO> getDoanhThuTheoThang(int year);

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

    HoaDon taoHoaDonCho();
    List<HoaDon> getHoaDonCho();

    void themSanPham(Long hoaDonId, ThemSanPhamRequest request);
    void capNhatSoLuong(Long hoaDonChiTietId,Integer soLuong);
    void doiKhachHang(Long hoaDonId, Long khachHangId);
    void xoaSanPham(Long hoaDonChiTietId);
    void thanhToan(Long hoaDonId, ThanhToanRequest request);
    List<HoaDon> getHoaDonOnline();
    List<HoaDonTaiQuayDTO> getHoaDonTaiQuay();

}