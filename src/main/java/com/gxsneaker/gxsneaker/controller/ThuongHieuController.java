package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.ThuongHieuDTO;
import com.gxsneaker.gxsneaker.entity.ThuongHieu;
import com.gxsneaker.gxsneaker.service.ThuongHieuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thuong-hieu")
@CrossOrigin(origins = "http://localhost:5173")
public class ThuongHieuController {

    private final ThuongHieuService service;

    public ThuongHieuController(ThuongHieuService service) {
        this.service = service;
    }

    @GetMapping
    public List<ThuongHieuDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ThuongHieuDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ThuongHieuDTO create(@RequestBody ThuongHieuDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ThuongHieuDTO update(@PathVariable Long id, @RequestBody ThuongHieuDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}