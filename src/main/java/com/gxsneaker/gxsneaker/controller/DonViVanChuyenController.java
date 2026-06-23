package com.gxsneaker.gxsneaker.controller;



import com.gxsneaker.gxsneaker.entity.DonViVanChuyen;
import com.gxsneaker.gxsneaker.repository.DonViVanChuyenRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/don-vi-van-chuyen")
@CrossOrigin(origins = "*")
public class DonViVanChuyenController {

    private final DonViVanChuyenRepository repository;

    public DonViVanChuyenController(
            DonViVanChuyenRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DonViVanChuyen> getAll() {
        return repository.findAll();
    }
}