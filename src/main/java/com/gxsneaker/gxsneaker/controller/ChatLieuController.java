package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.ChatLieuDTO;
import com.gxsneaker.gxsneaker.dto.XuatXuDTO;
import com.gxsneaker.gxsneaker.service.ChatLieuService;
import com.gxsneaker.gxsneaker.service.XuatXuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/chat-lieu")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatLieuController {

    private final ChatLieuService service;

    public ChatLieuController(ChatLieuService service) {
        this.service = service;
    }

    @GetMapping
    public List<ChatLieuDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ChatLieuDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ChatLieuDTO create(@RequestBody ChatLieuDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ChatLieuDTO update(@PathVariable Long id, @RequestBody ChatLieuDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
