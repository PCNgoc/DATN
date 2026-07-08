package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.*;
import com.gxsneaker.gxsneaker.entity.HoaDon;

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
}