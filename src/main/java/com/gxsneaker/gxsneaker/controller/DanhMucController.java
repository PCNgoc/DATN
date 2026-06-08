package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.DanhMucDTO;
import com.gxsneaker.gxsneaker.entity.DanhMuc;
import com.gxsneaker.gxsneaker.service.DanhMucService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danh-muc")
@CrossOrigin(origins = "http://localhost:5173")
public class DanhMucController {

    private final DanhMucService service;

    public DanhMucController(DanhMucService service) {
        this.service = service;
    }

    @GetMapping
    public List<DanhMucDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DanhMucDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public DanhMucDTO create(@RequestBody DanhMucDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public DanhMucDTO update(@PathVariable Long id, @RequestBody DanhMucDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}