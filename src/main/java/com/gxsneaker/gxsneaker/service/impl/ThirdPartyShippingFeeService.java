package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.DatHangItemDTO;
import com.gxsneaker.gxsneaker.dto.ShippingFeeQuoteDTO;
import com.gxsneaker.gxsneaker.dto.ShippingFeeRequestDTO;
import com.gxsneaker.gxsneaker.entity.ChiTietSanPham;
import com.gxsneaker.gxsneaker.repository.ChiTietSanPhamRepository;
import com.gxsneaker.gxsneaker.service.ShippingFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ThirdPartyShippingFeeService
        implements ShippingFeeService {

    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Value("${shipping.provider.enabled:false}")
    private boolean enabled;

    @Value("${shipping.provider.name:THIRD_PARTY}")
    private String providerName;

    @Value("${shipping.provider.base-url:}")
    private String baseUrl;

    @Value("${shipping.provider.fee-path:/shipping/fee}")
    private String feePath;

    @Value("${shipping.provider.api-key:}")
    private String apiKey;

    @Value("${shipping.provider.api-key-header:Authorization}")
    private String apiKeyHeader;

    @Value("${shipping.provider.api-key-prefix:Bearer}")
    private String apiKeyPrefix;

    @Value("${shipping.provider.default-item-weight-gram:1000}")
    private int defaultItemWeightGram;

    @Value("${shipping.provider.shop-id:}")
    private String shopId;

    @Value("${shipping.provider.service-type-id:2}")
    private Integer serviceTypeId;

    @Value("${shipping.provider.default-length-cm:35}")
    private Integer defaultLengthCm;

    @Value("${shipping.provider.default-width-cm:25}")
    private Integer defaultWidthCm;

    @Value("${shipping.provider.default-height-cm:15}")
    private Integer defaultHeightCm;

    @Override
    public ShippingFeeQuoteDTO calculateFee(
            ShippingFeeRequestDTO request
    ) {
        validateRequest(request);
        validateConfiguration();

        BigDecimal declaredValue = BigDecimal.ZERO;
        long totalWeightGram = 0;
        int totalQuantity = 0;

        for (DatHangItemDTO item : request.getItems()) {
            ChiTietSanPham product = chiTietSanPhamRepository
                    .findById(item.getChiTietSanPhamId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Không tìm thấy sản phẩm có ID "
                                            + item.getChiTietSanPhamId()
                            )
                    );

            int quantity = item.getSoLuong();

            totalQuantity += quantity;
            totalWeightGram +=
                    (long) defaultItemWeightGram * quantity;

            declaredValue = declaredValue.add(
                    product.getGiaBan().multiply(
                            BigDecimal.valueOf(quantity)
                    )
            );
        }

        Map<String, Object> providerRequest = new LinkedHashMap<>();

        providerRequest.put(
                "service_type_id",
                serviceTypeId
        );

        providerRequest.put(
                "to_district_id",
                Integer.parseInt(request.getDistrictCode())
        );

        providerRequest.put(
                "to_ward_code",
                request.getWardCode()
        );

        providerRequest.put(
                "length",
                defaultLengthCm
        );

        providerRequest.put(
                "width",
                defaultWidthCm
        );

        providerRequest.put(
                "height",
                defaultHeightCm
        );

        providerRequest.put(
                "weight",
                Math.toIntExact(totalWeightGram)
        );

        providerRequest.put(
                "insurance_value",
                declaredValue
                        .min(BigDecimal.valueOf(5_000_000))
                        .intValue()
        );

        providerRequest.put("coupon", null);

        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl(baseUrl.trim())
                    .build();

            Map<?, ?> response = restClient.post()
                    .uri(normalizeFeePath())
                    .headers(this::addAuthenticationHeader)
                    .body(providerRequest)
                    .retrieve()
                    .body(Map.class);

            BigDecimal fee = extractFee(response);

            if (fee == null || fee.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException(
                        "GHN trả về phí vận chuyển không hợp lệ"
                );
            }

            return ShippingFeeQuoteDTO.builder()
                    .provider("GHN")
                    .quoteId(null)
                    .fee(fee)
                    .currency("VND")
                    .build();

        } catch (RestClientException e) {
            throw new RuntimeException(
                    "Không thể lấy phí vận chuyển từ GHN: "
                            + e.getMessage()
            );
        }
    }

    private BigDecimal extractFee(Map<?, ?> response) {
        if (response == null) {
            return null;
        }

        Object dataObject = response.get("data");

        if (!(dataObject instanceof Map<?, ?> data)) {
            return null;
        }

        Object totalObject = data.get("total");

        if (totalObject == null) {
            return null;
        }

        try {
            return new BigDecimal(String.valueOf(totalObject));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String extractQuoteId(Map<?, ?> response) {
        return null;
    }

    private Object findValue(
            Map<?, ?> response,
            List<String> keys
    ) {
        if (response == null) {
            return null;
        }

        for (String key : keys) {
            if (response.containsKey(key)) {
                return response.get(key);
            }
        }

        for (String parent : List.of("data", "result", "quote")) {
            Object nestedValue = response.get(parent);

            if (nestedValue instanceof Map<?, ?> nestedMap) {
                for (String key : keys) {
                    if (nestedMap.containsKey(key)) {
                        return nestedMap.get(key);
                    }
                }
            }
        }

        return null;
    }

    private void validateRequest(ShippingFeeRequestDTO request) {
        if (request == null) {
            throw new RuntimeException(
                    "Dữ liệu tính phí vận chuyển không hợp lệ"
            );
        }

        if (isBlank(request.getProvinceCode())) {
            throw new RuntimeException("Thiếu mã tỉnh/thành phố");
        }

        if (isBlank(request.getDistrictCode())) {
            throw new RuntimeException("Thiếu mã quận/huyện");
        }

        if (isBlank(request.getWardCode())) {
            throw new RuntimeException("Thiếu mã phường/xã");
        }

        if (isBlank(request.getAddress())) {
            throw new RuntimeException("Thiếu địa chỉ giao hàng");
        }

        if (request.getItems() == null ||
                request.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Đơn hàng chưa có sản phẩm"
            );
        }

        for (DatHangItemDTO item : request.getItems()) {
            if (item == null ||
                    item.getChiTietSanPhamId() == null ||
                    item.getChiTietSanPhamId() <= 0) {
                throw new RuntimeException(
                        "Sản phẩm tính phí không hợp lệ"
                );
            }

            if (item.getSoLuong() == null ||
                    item.getSoLuong() <= 0) {
                throw new RuntimeException(
                        "Số lượng sản phẩm không hợp lệ"
                );
            }
        }
    }

    private void validateConfiguration() {
        if (!enabled) {
            throw new RuntimeException(
                    "Dịch vụ tính phí vận chuyển chưa được bật"
            );
        }

        if (isBlank(baseUrl)) {
            throw new RuntimeException(
                    "Chưa cấu hình URL đơn vị vận chuyển"
            );
        }
    }

    private String normalizeFeePath() {
        if (isBlank(feePath)) {
            return "/shipping/fee";
        }

        return feePath.startsWith("/")
                ? feePath
                : "/" + feePath;
    }

    private void addAuthenticationHeader(HttpHeaders headers) {
        if (isBlank(apiKey)) {
            throw new RuntimeException("Chưa cấu hình GHN Token");
        }

        if (isBlank(shopId)) {
            throw new RuntimeException("Chưa cấu hình GHN ShopId");
        }

        headers.set("Token", apiKey.trim());
        headers.set("ShopId", shopId.trim());
        headers.setContentType(
                org.springframework.http.MediaType.APPLICATION_JSON
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}