package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.GioHang;
import com.gxsneaker.gxsneaker.entity.GioHangChiTiet;
import com.gxsneaker.gxsneaker.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gio-hang")
@CrossOrigin("*")
public class GioHangController {

    @Autowired
    private GioHangService gioHangService;

    @GetMapping("/khach-hang/{khachHangId}")
    public ResponseEntity<GioHang> getCartByKhachHangId(@PathVariable Integer khachHangId) {
        return ResponseEntity.ok(gioHangService.getGioHangByKhachHangId(khachHangId));
    }

    @PostMapping("/khach-hang/{khachHangId}/add")
    public ResponseEntity<GioHangChiTiet> addItemToCart(
            @PathVariable Integer khachHangId,
            @RequestParam Long chiTietSanPhamId,
            @RequestParam Integer soLuong) {
        return ResponseEntity.ok(gioHangService.addItemToCart(khachHangId, chiTietSanPhamId, soLuong));
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<GioHangChiTiet> updateItemQuantity(
            @PathVariable Integer cartItemId,
            @RequestParam Integer soLuong) {
        return ResponseEntity.ok(gioHangService.updateItemQuantity(cartItemId, soLuong));
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Integer cartItemId) {
        gioHangService.removeItemFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/khach-hang/{khachHangId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Integer khachHangId) {
        gioHangService.clearCart(khachHangId);
        return ResponseEntity.noContent().build();
    }
}
