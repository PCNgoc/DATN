package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.*;
import com.gxsneaker.gxsneaker.entity.HoaDon;
import com.gxsneaker.gxsneaker.repository.ChiTietSanPhamRepository;
import com.gxsneaker.gxsneaker.repository.HoaDonChiTietRepository;
import com.gxsneaker.gxsneaker.repository.HoaDonRepository;
import com.gxsneaker.gxsneaker.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import com.gxsneaker.gxsneaker.entity.ChiTietSanPham;
import com.gxsneaker.gxsneaker.entity.HoaDonChiTiet;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

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

    @Override
    public List<TopSanPhamBanChayDTO> getTop5SanPhamBanChay(int year) {

        List<Object[]> data =
                hoaDonRepository.getTop5SanPhamBanChay(year);

        return data.stream()
                .map(item -> new TopSanPhamBanChayDTO(
                        (String) item[0],
                        ((Number) item[1]).longValue()
                ))
                .toList();
    }

    @Override
    @Transactional
    public HoaDon datHang(DatHangRequestDTO request) {
        System.out.println(request.getIdKhachHang());
        HoaDon hoaDon = new HoaDon();

        hoaDon.setIdKhachHang(request.getIdKhachHang());

        hoaDon.setLoaiDon("ONLINE");

        hoaDon.setTrangThai("CHO_XAC_NHAN");

        hoaDon.setNgayDatHang(new Date());

        hoaDon.setNgayTao(new Date());

        hoaDon.setNguoiTao("CUSTOMER");

        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());

        hoaDon.setSoDienThoaiNguoiNhan(request.getSoDienThoai());

        hoaDon.setDiaChiNguoiNhan(request.getDiaChi());

        hoaDon.setGhiChu(request.getGhiChu());

        Long count = hoaDonRepository.countHoaDon() + 1;

        hoaDon.setMaHoaDon(
                String.format("HD%06d", count)
        );

        BigDecimal tongTien = BigDecimal.ZERO;

        hoaDon = hoaDonRepository.save(hoaDon);

        for (DatHangItemDTO item : request.getItems()) {

            ChiTietSanPham ctsp =
                    chiTietSanPhamRepository
                            .findById(item.getChiTietSanPhamId())
                            .orElseThrow(
                                    () -> new RuntimeException("Không tìm thấy sản phẩm")
                            );

            if (ctsp.getSoLuongTon() < item.getSoLuong()) {

                throw new RuntimeException(
                        ctsp.getSanPham().getTenSanPham()
                                + " không đủ tồn kho"
                );

            }

            BigDecimal thanhTien =
                    ctsp.getGiaBan()
                            .multiply(
                                    BigDecimal.valueOf(item.getSoLuong())
                            );

            HoaDonChiTiet hdct =
                    HoaDonChiTiet.builder()
                            .hoaDon(hoaDon)
                            .chiTietSanPham(ctsp)
                            .soLuong(item.getSoLuong())
                            .donGia(ctsp.getGiaBan())
                            .thanhTien(thanhTien)
                            .build();

            hoaDonChiTietRepository.save(hdct);

            ctsp.setSoLuongTon(
                    ctsp.getSoLuongTon()
                            - item.getSoLuong()
            );

            chiTietSanPhamRepository.save(ctsp);

            tongTien =
                    tongTien.add(thanhTien);

        }

        hoaDon.setTongTienHang(tongTien);

        hoaDon.setSoTienGiam(BigDecimal.ZERO);

        hoaDon.setPhiVanChuyen(BigDecimal.valueOf(30000));

        hoaDon.setTongTienThanhToan(

                tongTien.add(
                        BigDecimal.valueOf(30000)
                )

        );

        return hoaDonRepository.save(hoaDon);

    }

    @Override
    public List<OrderResponseDTO> getOrdersByCustomer(Long customerId) {

        List<HoaDon> hoaDons =
                hoaDonRepository.findByIdKhachHang(customerId);

        return hoaDons.stream().map(hoaDon -> {

            List<OrderItemResponseDTO> items =
                    hoaDon.getHoaDonChiTiets()
                            .stream()
                            .map(ct -> {

                                return OrderItemResponseDTO.builder()

                                        .chiTietSanPhamId(
                                                ct.getChiTietSanPham().getId()
                                        )

                                        .productName(
                                                ct.getChiTietSanPham()
                                                        .getSanPham()
                                                        .getTenSanPham()
                                        )

                                        .image(
                                                ct.getChiTietSanPham()
                                                        .getSanPham()
                                                        .getAnhDaiDien()
                                        )

                                        .color(
                                                ct.getChiTietSanPham()
                                                        .getMauSac()
                                                        .getTen()
                                        )

                                        .size(
                                                ct.getChiTietSanPham()
                                                        .getKichThuoc()
                                                        .getSize()
                                        )

                                        .quantity(ct.getSoLuong())

                                        .price(ct.getDonGia())

                                        .total(ct.getThanhTien())

                                        .build();

                            }).toList();

            return OrderResponseDTO.builder()

                    .id(hoaDon.getId())

                    .maHoaDon(hoaDon.getMaHoaDon())

                    .ngayDatHang(hoaDon.getNgayDatHang())

                    .trangThai(hoaDon.getTrangThai())

                    .tenNguoiNhan(
                            hoaDon.getTenNguoiNhan()
                    )

                    .soDienThoai(
                            hoaDon.getSoDienThoaiNguoiNhan()
                    )

                    .diaChi(
                            hoaDon.getDiaChiNguoiNhan()
                    )

                    .tongTien(
                            hoaDon.getTongTienThanhToan()
                    )

                    .items(items)

                    .build();

        }).toList();
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        return convertToDTO(hoaDon);
    }

    private OrderResponseDTO convertToDTO(HoaDon hd) {

        List<OrderItemResponseDTO> items =
                hd.getHoaDonChiTiets()
                        .stream()
                        .map(ct -> OrderItemResponseDTO.builder()
                                .chiTietSanPhamId(
                                        ct.getChiTietSanPham().getId()
                                )
                                .productName(
                                        ct.getChiTietSanPham()
                                                .getSanPham()
                                                .getTenSanPham()
                                )
                                .image(
                                        ct.getChiTietSanPham()
                                                .getSanPham()
                                                .getAnhDaiDien()
                                )
                                .color(
                                        ct.getChiTietSanPham()
                                                .getMauSac()
                                                .getTen()
                                )
                                .size(
                                        ct.getChiTietSanPham()
                                                .getKichThuoc()
                                                .getSize()
                                )
                                .quantity(ct.getSoLuong())
                                .price(ct.getDonGia())
                                .total(ct.getThanhTien())
                                .build())
                        .toList();

        return OrderResponseDTO.builder()
                .id(hd.getId())
                .maHoaDon(hd.getMaHoaDon())
                .ngayDatHang(hd.getNgayDatHang())
                .trangThai(hd.getTrangThai())
                .tenNguoiNhan(hd.getTenNguoiNhan())
                .soDienThoai(hd.getSoDienThoaiNguoiNhan())
                .diaChi(hd.getDiaChiNguoiNhan())
                .tongTien(hd.getTongTienThanhToan())
                .items(items)
                .build();
    }

}