package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.SanPhamDTO;
import com.gxsneaker.gxsneaker.service.SanPhamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {

    private final SanPhamService sanPhamService;

    public ProductController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping
    public List<SanPhamDTO> getAll() {
        return sanPhamService.getAll();
    }

    @GetMapping("/{id}")
    public SanPhamDTO getById(
            @PathVariable Long id
    ) {
        return sanPhamService.getById(id);
    }
    @GetMapping("/search")
    public List<SanPhamDTO> search(
            @RequestParam String keyword
    ){
        return sanPhamService.search(keyword);
    }
    @GetMapping("/newest")
    public List<SanPhamDTO> getNewestProducts() {
        return sanPhamService.getNewestProducts();
    }
}