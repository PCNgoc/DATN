package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.CoGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoGiayRepository extends JpaRepository<CoGiay, Long>
{
    @Query("""
        SELECT MAX(cg.ma)
        FROM CoGiay cg
        WHERE cg.ma LIKE 'CG%'
        """)
    String getMaxMa();
}
