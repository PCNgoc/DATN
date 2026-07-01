package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.HoaDon;
import com.gxsneaker.gxsneaker.repository.HoaDonRepository;
import com.gxsneaker.gxsneaker.service.impl.PayOSPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PayOSCheckController {

    private final HoaDonRepository hoaDonRepository;
    private final PayOSPaymentService payOSPaymentService;

    @GetMapping("/check-order/{hoaDonId}")
    @Transactional
    public ResponseEntity<?> checkOrder(@PathVariable Long hoaDonId) {
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (hoaDon.getPayosOrderCode() == null) {
            throw new RuntimeException("Hóa đơn không có mã payOS");
        }

        Map<String, Object> payosData =
                payOSPaymentService.getPaymentLinkInfo(hoaDon.getPayosOrderCode());

        String payosStatus = String.valueOf(payosData.get("status"));

        Object amountObj = payosData.get("amount");
        Long payosAmount = amountObj == null ? 0L : Long.valueOf(String.valueOf(amountObj));

        Long orderAmount = hoaDon.getTongTienThanhToan().longValue();

        boolean daThanhToan = "PAID".equalsIgnoreCase(payosStatus);
        boolean dungSoTien = payosAmount.equals(orderAmount);

        boolean donChuaBiHuy =
                !"DA_HUY".equalsIgnoreCase(String.valueOf(hoaDon.getTrangThai()))
                        && !"HUY".equalsIgnoreCase(String.valueOf(hoaDon.getTrangThai()));

        boolean donDangChoThanhToan =
                "CHO_THANH_TOAN".equalsIgnoreCase(String.valueOf(hoaDon.getTrangThaiThanhToan()));

        if (daThanhToan && dungSoTien && donChuaBiHuy && donDangChoThanhToan) {
            hoaDon.setTrangThaiThanhToan("DA_THANH_TOAN");
            hoaDon.setTrangThai("CHO_XAC_NHAN");
            hoaDon.setNgayThanhToan(new Date());
            hoaDon.setNgayCapNhat(new Date());

            if (payosData.get("paymentLinkId") != null) {
                hoaDon.setPaymentLinkId(String.valueOf(payosData.get("paymentLinkId")));
            }

            hoaDonRepository.save(hoaDon);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", hoaDon.getId());
        result.put("maHoaDon", hoaDon.getMaHoaDon());
        result.put("payosStatus", payosStatus);
        result.put("trangThai", hoaDon.getTrangThai());
        result.put("trangThaiThanhToan", hoaDon.getTrangThaiThanhToan());
        result.put("tongTienThanhToan", hoaDon.getTongTienThanhToan());

        return ResponseEntity.ok(result);
    }
}