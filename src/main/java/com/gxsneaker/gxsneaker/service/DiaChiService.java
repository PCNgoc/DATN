package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.entity.DiaChi;
import java.util.List;
import java.util.Optional;

public interface DiaChiService {
    List<DiaChi> getDiaChiByKhachHangId(Integer khachHangId);
    Optional<DiaChi> getDiaChiById(Integer id);
    DiaChi createDiaChi(DiaChi diaChi);
    DiaChi updateDiaChi(Integer id, DiaChi diaChi);
    void deleteDiaChi(Integer id);
}
