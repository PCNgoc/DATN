package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.PhieuGiamGia;
import com.gxsneaker.gxsneaker.service.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-giam-gia")
@CrossOrigin("*")
public class PhieuGiamGiaController {

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @GetMapping
    public List<PhieuGiamGia> getAllPhieuGiamGia() {
        return phieuGiamGiaService.getAllPhieuGiamGia();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhieuGiamGia> getPhieuGiamGiaById(@PathVariable Integer id) {
        return phieuGiamGiaService.getPhieuGiamGiaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ma/{maPhieu}")
    public ResponseEntity<PhieuGiamGia> getPhieuGiamGiaByMa(@PathVariable String maPhieu) {
        return phieuGiamGiaService.getPhieuGiamGiaByMa(maPhieu)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PhieuGiamGia createPhieuGiamGia(@RequestBody PhieuGiamGia phieuGiamGia) {
        return phieuGiamGiaService.createPhieuGiamGia(phieuGiamGia);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhieuGiamGia> updatePhieuGiamGia(@PathVariable Integer id, @RequestBody PhieuGiamGia phieuGiamGia) {
        try {
            PhieuGiamGia updated = phieuGiamGiaService.updatePhieuGiamGia(id, phieuGiamGia);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhieuGiamGia(@PathVariable Integer id) {
        phieuGiamGiaService.deletePhieuGiamGia(id);
        return ResponseEntity.noContent().build();
    }
}
