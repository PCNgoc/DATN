package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.ChiTietSanPhamDTO;
import com.gxsneaker.gxsneaker.dto.GenerateVariantDTO;
import com.gxsneaker.gxsneaker.service.ChiTietSanPhamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chi-tiet-san-pham")
@CrossOrigin("*")
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService service;

    public ChiTietSanPhamController(
            ChiTietSanPhamService service
    ) {
        this.service = service;
    }

    @GetMapping
    public List<ChiTietSanPhamDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ChiTietSanPhamDTO getById(
            @PathVariable Long id
    ) {
        return service.getById(id);
    }

    @GetMapping("/san-pham/{id}")
    public List<ChiTietSanPhamDTO>
    getBySanPham(
            @PathVariable Long id
    ){
        return service.getBySanPham(id);
    }

    @GetMapping("/user/san-pham/{id}")
    public List<ChiTietSanPhamDTO> getBySanPhamUser(
            @PathVariable Long id
    ){
        return service.getBySanPhamUser(id);
    }

    @PostMapping
    public ChiTietSanPhamDTO create(
            @RequestBody ChiTietSanPhamDTO dto
    ){
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ChiTietSanPhamDTO update(
            @PathVariable Long id,
            @RequestBody ChiTietSanPhamDTO dto
    ){
        return service.update(id,dto);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ){
        service.delete(id);
    }
    @PostMapping("/generate")
    public void generateVariants(
            @RequestBody GenerateVariantDTO dto
    ){
        service.generateVariants(dto);
    }
}