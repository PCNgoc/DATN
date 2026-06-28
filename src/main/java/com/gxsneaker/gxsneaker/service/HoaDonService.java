package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.*;
import com.gxsneaker.gxsneaker.entity.HoaDon;

import java.util.List;

public interface HoaDonService {

    List<DoanhThuTheoThangDTO> getDoanhThuTheoThang(int year);

    List<TrangThaiDonHangDTO> getThongKeTrangThaiDonHang(int year);

    List<TopSanPhamBanChayDTO> getTop5SanPhamBanChay(Integer year);

    // Đặt haàng
    HoaDon datHang(DatHangRequestDTO request);
    //Chi tiết đơn
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByCustomer(Long customerId);

}