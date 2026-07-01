package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.entity.HoaDon;
import com.gxsneaker.gxsneaker.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PayOSWebhookController {

    private final HoaDonRepository hoaDonRepository;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @PostMapping("/webhook")
    @Transactional
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> body) {
        try {
            Object dataObj = body.get("data");

            if (!(dataObj instanceof Map<?, ?> rawData)) {
                return ResponseEntity.badRequest().body("Missing data");
            }

            Map<String, Object> data = new TreeMap<>();
            rawData.forEach((key, value) -> data.put(String.valueOf(key), value));

            String signature = Objects.toString(body.get("signature"), "");

            if (!isValidSignature(data, signature)) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            boolean success = Boolean.TRUE.equals(body.get("success"));
            String outerCode = Objects.toString(body.get("code"), "");
            String innerCode = Objects.toString(data.get("code"), "");

            if (!success || !"00".equals(outerCode) || !"00".equals(innerCode)) {
                return ResponseEntity.ok("IGNORED");
            }

            Long orderCode = Long.valueOf(String.valueOf(data.get("orderCode")));

            HoaDon hoaDon = hoaDonRepository.findByPayosOrderCode(orderCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn payOS"));

            BigDecimal paidAmount = new BigDecimal(String.valueOf(data.get("amount")));

            if (hoaDon.getTongTienThanhToan().compareTo(paidAmount) != 0) {
                return ResponseEntity
                        .badRequest()
                        .body("Số tiền thanh toán không khớp");
            }

            if ("DA_THANH_TOAN".equals(hoaDon.getTrangThaiThanhToan())) {
                return ResponseEntity.ok("OK");
            }

            if ("DA_HUY".equals(hoaDon.getTrangThai())) {
                return ResponseEntity
                        .badRequest()
                        .body("Đơn hàng đã bị hủy");
            }

            hoaDon.setTrangThaiThanhToan("DA_THANH_TOAN");
            hoaDon.setTrangThai("CHO_XAC_NHAN");
            hoaDon.setNgayThanhToan(new Date());
            hoaDon.setNgayCapNhat(new Date());
            hoaDon.setPaymentLinkId(
                    Objects.toString(data.get("paymentLinkId"), hoaDon.getPaymentLinkId())
            );

            hoaDonRepository.save(hoaDon);

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean isValidSignature(Map<String, Object> data, String signature) throws Exception {
        String rawData = data.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + normalizeValue(entry.getValue()))
                .collect(Collectors.joining("&"));

        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey =
                new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        hmac.init(secretKey);

        byte[] hash = hmac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
        String calculatedSignature = bytesToHex(hash);

        return MessageDigest.isEqual(
                calculatedSignature.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String normalizeValue(Object value) {
        if (value == null) {
            return "";
        }

        String result = String.valueOf(value);

        if ("null".equalsIgnoreCase(result) || "undefined".equalsIgnoreCase(result)) {
            return "";
        }

        return result;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}