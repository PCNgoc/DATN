package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.ChatLieuDTO;
import com.gxsneaker.gxsneaker.entity.ChatLieu;
import com.gxsneaker.gxsneaker.entity.XuatXu;
import com.gxsneaker.gxsneaker.mapper.ChatLieuMapper;
import com.gxsneaker.gxsneaker.mapper.XuatXuMapper;
import com.gxsneaker.gxsneaker.repository.ChatLieuRepository;
import com.gxsneaker.gxsneaker.service.ChatLieuService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;

@Service
public class ChatLieuServiceImpl implements ChatLieuService {

    private ChatLieuRepository chatLieuRepository;

    public ChatLieuServiceImpl(ChatLieuRepository chatLieuRepository) {
        this.chatLieuRepository = chatLieuRepository;
    }

    @Override
    public List<ChatLieuDTO> getAll() {
        return chatLieuRepository.findAll()
                .stream().map(ChatLieuMapper::toDTO)
                .toList();
    }

    @Override
    public ChatLieuDTO getById(Long id) {
        return chatLieuRepository.findById(id).map(ChatLieuMapper::toDTO).orElse(null);
    }

    @Override
    public ChatLieuDTO create(ChatLieuDTO dto) {
        ChatLieu chatLieu = ChatLieuMapper.toEntity(dto);
        chatLieuRepository.save(chatLieu);
        return ChatLieuMapper.toDTO(chatLieu);
    }

    @Override
    public ChatLieuDTO update(Long id, ChatLieuDTO dto) {
        ChatLieu old = chatLieuRepository.findById(id).orElse(null);

        if (old == null) return null;

        old.setMa(dto.getMa());
        old.setTen(dto.getTen());
        old.setMoTa(dto.getMoTa());
        old.setTrangThai(dto.getTrangThai());


        return ChatLieuMapper.toDTO(
                chatLieuRepository.save(old)
        );
    }


    @Override
    public void delete(Long id) {
                 chatLieuRepository.deleteById(id);
    }
}
