package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.XuatXu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface XuatXuRepository extends JpaRepository<XuatXu,Long> {
    @Query("""
        SELECT MAX(xx.ma)
        FROM XuatXu xx
        WHERE xx.ma LIKE 'XX%'
        """)
    String getMaxMa();
}
