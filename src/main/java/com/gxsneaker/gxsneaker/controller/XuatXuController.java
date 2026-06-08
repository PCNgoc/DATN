package com.gxsneaker.gxsneaker.controller;


import com.gxsneaker.gxsneaker.dto.DanhMucDTO;
import com.gxsneaker.gxsneaker.dto.XuatXuDTO;
import com.gxsneaker.gxsneaker.service.DanhMucService;
import com.gxsneaker.gxsneaker.service.XuatXuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/xuat-xu")
@CrossOrigin(origins = "http://localhost:5173")
public class XuatXuController {

    private final XuatXuService service;

    public XuatXuController(XuatXuService service) {
        this.service = service;
    }

    @GetMapping
    public List<XuatXuDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public XuatXuDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public XuatXuDTO create(@RequestBody XuatXuDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public XuatXuDTO update(@PathVariable Long id, @RequestBody XuatXuDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
