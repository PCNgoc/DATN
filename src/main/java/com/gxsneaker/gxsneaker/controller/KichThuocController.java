package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.KichThuocDTO;
import com.gxsneaker.gxsneaker.service.KichThuocService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kich-thuoc")
@CrossOrigin(origins = "http://localhost:5173")
public class KichThuocController {

    private final KichThuocService kichThuocService;

    public KichThuocController(KichThuocService kichThuocService) {
        this.kichThuocService = kichThuocService;
    }

    @GetMapping
    public List<KichThuocDTO> getAll() {
        return kichThuocService.getAll();
    }

    @GetMapping("/{id}")
    public KichThuocDTO getById(@PathVariable Long id) {
        return kichThuocService.getById(id);
    }

    @PostMapping
    public KichThuocDTO create(@RequestBody KichThuocDTO dto) {
        return kichThuocService.create(dto);
    }

    @PutMapping("/{id}")
    public KichThuocDTO update(@PathVariable Long id,
                               @RequestBody KichThuocDTO dto) {
        return kichThuocService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        kichThuocService.delete(id);
    }
}