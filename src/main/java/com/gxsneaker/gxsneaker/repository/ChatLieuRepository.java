package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatLieuRepository extends JpaRepository<ChatLieu,Long> {
    @Query("""
        SELECT MAX(cl.ma)
        FROM ChatLieu cl
        WHERE cl.ma LIKE 'CL%'
        """)
    String getMaxMa();
}
