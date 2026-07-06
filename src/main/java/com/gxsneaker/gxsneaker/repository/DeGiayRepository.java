package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.DeGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeGiayRepository extends JpaRepository<DeGiay,Long> {
    @Query("""
        SELECT MAX(dg.ma)
        FROM DeGiay dg
        WHERE dg.ma LIKE 'DG%'
        """)
    String getMaxMa();
}
