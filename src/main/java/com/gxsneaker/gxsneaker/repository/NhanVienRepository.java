package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    Optional<NhanVien> findByEmail(String email);

    Optional<NhanVien> findBySoDienThoai(String soDienThoai);
}