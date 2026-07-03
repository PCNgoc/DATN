package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.entity.HoaDon;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayOSPaymentService {

    private final PayOS payOS;

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public CreatePaymentLinkResponse createPaymentLink(HoaDon hoaDon) throws Exception {
        Long expiredAt = hoaDon.getHanThanhToan() == null
                ? null
                : hoaDon.getHanThanhToan().getTime() / 1000;

        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(hoaDon.getPayosOrderCode())
                .amount(hoaDon.getTongTienThanhToan().longValue())
                .description("GX" + hoaDon.getId())
                .returnUrl(frontendUrl + "/order-success/" + hoaDon.getId())
                .cancelUrl(frontendUrl + "/payos-waiting/" + hoaDon.getId() + "?cancel=true")
                .expiredAt(expiredAt)
                .buyerName(hoaDon.getTenNguoiNhan())
                .buyerPhone(hoaDon.getSoDienThoaiNguoiNhan())
                .buyerAddress(hoaDon.getDiaChiNguoiNhan())
                .build();

        return payOS.paymentRequests().create(request);
    }

    public void cancelPaymentLink(Long orderCode, String reason) {
        try {
            if (orderCode != null) {
                payOS.paymentRequests().cancel(orderCode, reason);
            }
        } catch (Exception e) {
            System.err.println("Không thể hủy link payOS: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getPaymentLinkInfo(Long orderCode) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-client-id", clientId);
            headers.set("x-api-key", apiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode;

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();

            if (body == null || body.get("data") == null) {
                throw new RuntimeException("Không lấy được thông tin thanh toán payOS");
            }

            return (Map<String, Object>) body.get("data");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kiểm tra trạng thái payOS: " + e.getMessage());
        }
    }
}