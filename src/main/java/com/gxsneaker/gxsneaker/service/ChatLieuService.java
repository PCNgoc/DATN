package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.ChatLieuDTO;
import com.gxsneaker.gxsneaker.dto.XuatXuDTO;
import com.gxsneaker.gxsneaker.entity.ChatLieu;

import java.util.List;

public interface ChatLieuService {
    List<ChatLieuDTO> getAll();

    ChatLieuDTO getById(Long id);

    ChatLieuDTO create(ChatLieuDTO dto);

    ChatLieuDTO update(Long id, ChatLieuDTO dto);

    void delete(Long id);
}
