package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.DiaChi;
import com.gxsneaker.gxsneaker.service.DiaChiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dia-chi")
@CrossOrigin("*")
public class DiaChiController {

    @Autowired
    private DiaChiService diaChiService;

    @GetMapping("/khach-hang/{khachHangId}")
    public List<DiaChi> getDiaChiByKhachHangId(@PathVariable Integer khachHangId) {
        return diaChiService.getDiaChiByKhachHangId(khachHangId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaChi> getDiaChiById(@PathVariable Integer id) {
        return diaChiService.getDiaChiById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DiaChi createDiaChi(@RequestBody DiaChi diaChi) {
        return diaChiService.createDiaChi(diaChi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaChi> updateDiaChi(@PathVariable Integer id, @RequestBody DiaChi diaChi) {
        try {
            DiaChi updated = diaChiService.updateDiaChi(id, diaChi);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiaChi(@PathVariable Integer id) {
        diaChiService.deleteDiaChi(id);
        return ResponseEntity.noContent().build();
    }
}
