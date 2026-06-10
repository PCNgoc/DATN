package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.ChatLieuDTO;
import com.gxsneaker.gxsneaker.dto.CoGiayDTO;
import com.gxsneaker.gxsneaker.service.ChatLieuService;
import com.gxsneaker.gxsneaker.service.CoGiayService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/co-giay")
@CrossOrigin(origins = "http://localhost:5173")
public class CoGiayController {
    private final CoGiayService service;

    public CoGiayController(CoGiayService service) {

        this.service = service;
    }

    @GetMapping
    public List<CoGiayDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CoGiayDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public CoGiayDTO create(@RequestBody CoGiayDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CoGiayDTO update(@PathVariable Long id, @RequestBody CoGiayDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
