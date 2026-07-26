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
    public List<SanPhamDTO> getAll(

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) Long brandId,

            @RequestParam(required = false) Long categoryId,

            @RequestParam(required = false) Long xuatXuId,

            @RequestParam(required = false) Long chatLieuId,

            @RequestParam(required = false) Long coGiayId,

            @RequestParam(required = false) Long deGiayId,

            @RequestParam(required = false) String gioiTinh

    ) {

        return sanPhamService.filter(
                keyword,
                brandId,
                categoryId,
                xuatXuId,
                chatLieuId,
                coGiayId,
                deGiayId,
                gioiTinh
        );

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