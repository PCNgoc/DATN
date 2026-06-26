package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.entity.PhieuGiamGiaKhachHang;
import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaKhachHangService {
    List<PhieuGiamGiaKhachHang> getVouchersByKhachHangId(Integer khachHangId);
    PhieuGiamGiaKhachHang assignVoucherToKhachHang(Integer khachHangId, Integer phieuGiamGiaId);
    PhieuGiamGiaKhachHang markAsUsed(Integer id);
    Optional<PhieuGiamGiaKhachHang> getById(Integer id);
}
