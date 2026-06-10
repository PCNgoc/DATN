package com.gxsneaker.gxsneaker.mapper;


import com.gxsneaker.gxsneaker.dto.ChatLieuDTO;
import com.gxsneaker.gxsneaker.entity.ChatLieu;

public class ChatLieuMapper {
    public static ChatLieuDTO toDTO(ChatLieu dm) {
        ChatLieuDTO dto = new ChatLieuDTO();

        dto.setId(dm.getId());
        dto.setMa(dm.getMa());
        dto.setTen(dm.getTen());
        dto.setMoTa(dm.getMoTa());
        dto.setTrangThai(dm.getTrangThai());

        return dto;
    }

    public static ChatLieu toEntity(ChatLieuDTO dto) {
        ChatLieu dm = new ChatLieu();

        dm.setId(dto.getId());
        dm.setMa(dto.getMa());
        dm.setTen(dto.getTen());
        dm.setMoTa(dto.getMoTa());
        dm.setTrangThai(dto.getTrangThai());

        return dm;
    }
}
