package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.KhachHang;
import com.gxsneaker.gxsneaker.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
@CrossOrigin("*")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping
    public List<KhachHang> getAllKhachHang() {
        return khachHangService.getAllKhachHang();
    }

    @GetMapping("/{id}")
    public ResponseEntity<KhachHang> getKhachHangById(@PathVariable Integer id) {
        return khachHangService.getKhachHangById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public KhachHang createKhachHang(@RequestBody KhachHang khachHang) {
        return khachHangService.createKhachHang(khachHang);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhachHang> updateKhachHang(@PathVariable Integer id, @RequestBody KhachHang khachHang) {
        try {
            KhachHang updatedKhachHang = khachHangService.updateKhachHang(id, khachHang);
            return ResponseEntity.ok(updatedKhachHang);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKhachHang(@PathVariable Integer id) {
        khachHangService.deleteKhachHang(id);
        return ResponseEntity.noContent().build();
    }
}
