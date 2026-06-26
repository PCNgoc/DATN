package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.entity.GioHang;
import com.gxsneaker.gxsneaker.entity.GioHangChiTiet;

public interface GioHangService {
    GioHang getGioHangByKhachHangId(Integer khachHangId);
    GioHangChiTiet addItemToCart(Integer khachHangId, Long chiTietSanPhamId, Integer soLuong);
    GioHangChiTiet updateItemQuantity(Integer cartItemId, Integer soLuong);
    void removeItemFromCart(Integer cartItemId);
    void clearCart(Integer khachHangId);
}
