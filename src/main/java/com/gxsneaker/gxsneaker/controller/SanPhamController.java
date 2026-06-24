package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.SanPhamDTO;
import com.gxsneaker.gxsneaker.service.SanPhamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham")
@CrossOrigin("*")
public class SanPhamController {

    private final SanPhamService sanPhamService;

    public SanPhamController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping
    public List<SanPhamDTO> getAll() {
        return sanPhamService.getAll();
    }

    @GetMapping("/{id}")
    public SanPhamDTO getById(@PathVariable Long id) {
        return sanPhamService.getById(id);
    }

    @PostMapping
    public SanPhamDTO create(@RequestBody SanPhamDTO dto) {
        return sanPhamService.create(dto);
    }

    @PutMapping("/{id}")
    public SanPhamDTO update(
            @PathVariable Long id,
            @RequestBody SanPhamDTO dto
    ) {
        return sanPhamService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sanPhamService.delete(id);
    }
    @GetMapping("/new")
    public List<SanPhamDTO> getNewProducts() {
        return sanPhamService.getNewProducts();
    }
    @GetMapping("/featured")
    public List<SanPhamDTO> getFeaturedProducts() {
        return sanPhamService.getFeaturedProducts();
    }
    @GetMapping("/newest")
    public List<SanPhamDTO> getNewestProducts() {
        return sanPhamService.getNewestProducts();
    }
}