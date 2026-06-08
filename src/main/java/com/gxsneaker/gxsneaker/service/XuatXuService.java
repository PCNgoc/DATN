package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.DanhMucDTO;
import com.gxsneaker.gxsneaker.dto.XuatXuDTO;
import com.gxsneaker.gxsneaker.entity.XuatXu;

import java.util.List;

public interface XuatXuService {

    List<XuatXuDTO> getAll();

    XuatXuDTO getById(Long id);

    XuatXuDTO create(XuatXuDTO dto);

    XuatXuDTO update(Long id, XuatXuDTO dto);

    void delete(Long id);
}
