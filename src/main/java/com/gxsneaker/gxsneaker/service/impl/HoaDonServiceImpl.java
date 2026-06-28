package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.DoanhThuTheoThangDTO;
import com.gxsneaker.gxsneaker.dto.TrangThaiDonHangDTO;
import com.gxsneaker.gxsneaker.repository.HoaDonRepository;
import com.gxsneaker.gxsneaker.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;

    @Override
    public List<DoanhThuTheoThangDTO> getDoanhThuTheoThang(int year) {

        List<Object[]> data =
                hoaDonRepository.getDoanhThuTheoThang(year);

        return data.stream()
                .map(item -> new DoanhThuTheoThangDTO(
                        ((Number) item[0]).intValue(),
                        (BigDecimal) item[1]
                ))
                .toList();
    }

    @Override
    public List<TrangThaiDonHangDTO> getThongKeTrangThaiDonHang(int year) {

        List<Object[]> data =
                hoaDonRepository.getThongKeTrangThaiDonHang(year);

        return data.stream()
                .map(item -> new TrangThaiDonHangDTO(
                        (String) item[0],
                        ((Number) item[1]).longValue()
                ))
                .toList();
    }
}