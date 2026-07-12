package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.config.VNPayConfig;
import com.gxsneaker.gxsneaker.dto.UpdateTrangThaiRequest;
import com.gxsneaker.gxsneaker.service.HoaDonService;
import com.gxsneaker.gxsneaker.service.VNPayService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;
    private final HoaDonService hoaDonService;
    private final VNPayConfig vnPayConfig;

    @GetMapping("/api/vnpay/return")
    public void vnpayReturn(
            @RequestParam Map<String, String> params,
            HttpServletResponse response
    ) throws IOException {

        boolean validSignature = vnPayService.verifyReturnUrl(params);

        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String txnRef = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");

        Long hoaDonId = vnPayService.extractHoaDonId(txnRef);

        if (validSignature && "00".equals(responseCode) && "00".equals(transactionStatus)) {
            hoaDonService.xacNhanThanhToanVnpay(hoaDonId, transactionNo);

            String url = vnPayConfig.getFrontendUrl()
                    + "/order-success/" + hoaDonId
                    + "?payment=VNPAY"
                    + "&vnp_ResponseCode=00";

            response.sendRedirect(url);
            return;
        }

        try {
            hoaDonService.huyDon(
                    hoaDonId,
                    "Thanh toán VNPAY thất bại hoặc bị hủy",
                    "VNPAY"
            );
        } catch (Exception ignored) {
        }

        String message = validSignature
                ? "Thanh toán VNPAY thất bại hoặc bị hủy"
                : "Sai chữ ký VNPAY";

        String url = vnPayConfig.getFrontendUrl()
                + "/checkout"
                + "?payment=VNPAY_FAILED"
                + "&message="
                + URLEncoder.encode(message, StandardCharsets.UTF_8);

        response.sendRedirect(url);
    }
}