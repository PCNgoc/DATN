package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietSanPhamRepository
        extends JpaRepository<ChiTietSanPham,Long> {

    List<ChiTietSanPham>
    findBySanPhamId(Long idSanPham);

}