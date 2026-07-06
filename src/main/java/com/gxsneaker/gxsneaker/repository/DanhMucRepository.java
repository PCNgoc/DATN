package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {
    @Query("""
        SELECT MAX(dm.ma)
        FROM DanhMuc dm
        WHERE dm.ma LIKE 'DM%'
        """)
    String getMaxMa();
}
