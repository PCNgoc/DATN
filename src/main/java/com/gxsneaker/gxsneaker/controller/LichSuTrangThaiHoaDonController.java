package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.LichSuTrangThaiHoaDon;
import com.gxsneaker.gxsneaker.repository.LichSuTrangThaiHoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lich-su-hoa-don")
@CrossOrigin("*")
public class LichSuTrangThaiHoaDonController {

    @Autowired
    private LichSuTrangThaiHoaDonRepository repository;

    @GetMapping
    public List<LichSuTrangThaiHoaDon> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public LichSuTrangThaiHoaDon getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public LichSuTrangThaiHoaDon create(
            @RequestBody LichSuTrangThaiHoaDon entity) {
        return repository.save(entity);
    }

    @PutMapping("/{id}")
    public LichSuTrangThaiHoaDon update(
            @PathVariable Long id,
            @RequestBody LichSuTrangThaiHoaDon entity) {

        entity.setId(id);
        return repository.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}