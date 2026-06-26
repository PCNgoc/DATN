package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.entity.KhachHang;
import java.util.List;
import java.util.Optional;

public interface KhachHangService {
    List<KhachHang> getAllKhachHang();
    Optional<KhachHang> getKhachHangById(Integer id);
    KhachHang createKhachHang(KhachHang khachHang);
    KhachHang updateKhachHang(Integer id, KhachHang khachHang);
    void deleteKhachHang(Integer id);
}
