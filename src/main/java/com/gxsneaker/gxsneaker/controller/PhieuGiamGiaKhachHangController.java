package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.PhieuGiamGiaKhachHang;
import com.gxsneaker.gxsneaker.service.PhieuGiamGiaKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-giam-gia-khach-hang")
@CrossOrigin("*")
public class PhieuGiamGiaKhachHangController {

    @Autowired
    private PhieuGiamGiaKhachHangService phieuGiamGiaKhachHangService;

    @GetMapping("/khach-hang/{khachHangId}")
    public List<PhieuGiamGiaKhachHang> getVouchersByKhachHangId(@PathVariable Integer khachHangId) {
        return phieuGiamGiaKhachHangService.getVouchersByKhachHangId(khachHangId);
    }

    @PostMapping("/assign")
    public ResponseEntity<PhieuGiamGiaKhachHang> assignVoucherToKhachHang(
            @RequestParam Integer khachHangId,
            @RequestParam Integer phieuGiamGiaId) {
        try {
            PhieuGiamGiaKhachHang result = phieuGiamGiaKhachHangService.assignVoucherToKhachHang(khachHangId, phieuGiamGiaId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/use")
    public ResponseEntity<PhieuGiamGiaKhachHang> useVoucher(@PathVariable Integer id) {
        try {
            PhieuGiamGiaKhachHang updated = phieuGiamGiaKhachHangService.markAsUsed(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
