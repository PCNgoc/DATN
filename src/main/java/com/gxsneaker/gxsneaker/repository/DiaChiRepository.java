package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Integer> {
}
