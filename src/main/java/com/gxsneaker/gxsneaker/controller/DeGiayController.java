package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.CoGiayDTO;
import com.gxsneaker.gxsneaker.dto.DeGiayDTO;
import com.gxsneaker.gxsneaker.service.CoGiayService;
import com.gxsneaker.gxsneaker.service.DeGiayService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/de-giay")
@CrossOrigin(origins = "http://localhost:5173")
public class DeGiayController {
    private final DeGiayService service;

    public DeGiayController(DeGiayService service) {

        this.service = service;
    }

    @GetMapping
    public List<DeGiayDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DeGiayDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public DeGiayDTO create(@RequestBody DeGiayDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public DeGiayDTO update(@PathVariable Long id, @RequestBody DeGiayDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
