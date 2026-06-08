package com.gxsneaker.gxsneaker.repository;

import com.gxsneaker.gxsneaker.entity.OtpXacThuc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpXacThucRepository extends JpaRepository<OtpXacThuc, Integer> {

    Optional<OtpXacThuc> findTopByEmailAndLoaiOtpAndDaSuDungOrderByIdDesc(
            String email,
            String loaiOtp,
            Boolean daSuDung
    );
}