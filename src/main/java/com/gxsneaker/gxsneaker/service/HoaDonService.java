package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.DoanhThuTheoThangDTO;
import com.gxsneaker.gxsneaker.dto.TopSanPhamBanChayDTO;
import com.gxsneaker.gxsneaker.dto.TrangThaiDonHangDTO;

import java.util.List;

public interface HoaDonService {

    List<DoanhThuTheoThangDTO> getDoanhThuTheoThang(int year);

    List<TrangThaiDonHangDTO> getThongKeTrangThaiDonHang(int year);

    List<TopSanPhamBanChayDTO> getTop5SanPhamBanChay(Integer year);

}