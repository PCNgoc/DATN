package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.HoaDonChiTiet;
import com.gxsneaker.gxsneaker.repository.HoaDonChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoa-don-chi-tiet")
@CrossOrigin("*")
public class HoaDonChiTietController {

    @Autowired
    private HoaDonChiTietRepository repository;

    @GetMapping
    public List<HoaDonChiTiet> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public HoaDonChiTiet getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public HoaDonChiTiet create(@RequestBody HoaDonChiTiet entity) {
        return repository.save(entity);
    }

    @PutMapping("/{id}")
    public HoaDonChiTiet update(@PathVariable Long id,
                                @RequestBody HoaDonChiTiet entity) {

        entity.setId(id);
        return repository.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}