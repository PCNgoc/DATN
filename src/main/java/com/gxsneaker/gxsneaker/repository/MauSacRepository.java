package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MauSacRepository extends JpaRepository<MauSac,Long> {
    @Query("""
        SELECT MAX(ms.ma)
        FROM MauSac ms
        WHERE ms.ma LIKE 'MS%'
        """)
    String getMaxMa();
}
