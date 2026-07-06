package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KichThuocRepository extends JpaRepository<KichThuoc, Long> {
    @Query("""
        SELECT MAX(kt.ma)
        FROM KichThuoc kt
        WHERE kt.ma LIKE 'KT%'
        """)
    String getMaxMa();
}