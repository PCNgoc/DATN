package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Long> {
    @Query("""
        SELECT MAX(th.ma)
        FROM ThuongHieu th
        WHERE th.ma LIKE 'TH%'
        """)
    String getMaxMa();

}