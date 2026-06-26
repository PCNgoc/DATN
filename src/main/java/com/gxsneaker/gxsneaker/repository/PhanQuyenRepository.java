package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.PhanQuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PhanQuyenRepository extends JpaRepository<PhanQuyen, Integer> {
    Optional<PhanQuyen> findByMaQuyen(String maQuyen);
}
