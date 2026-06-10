package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.MauSacDTO;
import com.gxsneaker.gxsneaker.service.MauSacService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mau-sac")
@CrossOrigin(origins = "http://localhost:5173")
public class MauSacController {

    private final MauSacService mauSacService;

    public MauSacController(MauSacService mauSacService) {
        this.mauSacService = mauSacService;
    }

    @GetMapping
    public List<MauSacDTO> getAll() {
        return mauSacService.getAll();
    }

    @GetMapping("/{id}")
    public MauSacDTO getById(@PathVariable Long id) {
        return mauSacService.getById(id);
    }

    @PostMapping
    public MauSacDTO create(@RequestBody MauSacDTO dto) {
        return mauSacService.create(dto);
    }

    @PutMapping("/{id}")
    public MauSacDTO update(@PathVariable Long id,
                            @RequestBody MauSacDTO dto) {
        return mauSacService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        mauSacService.delete(id);
    }
}