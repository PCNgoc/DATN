package com.gxsneaker.gxsneaker.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping/address")
@CrossOrigin("*")
public class GhnAddressController {

    @Value("${shipping.provider.base-url}")
    private String baseUrl;

    @Value("${shipping.provider.api-key}")
    private String apiKey;

    @GetMapping("/provinces")
    public ResponseEntity<?> getProvinces() {
        try {
            Map<?, ?> response = callGhn(
                    "/shiip/public-api/master-data/province",
                    Map.of()
            );

            return ResponseEntity.ok(
                    normalizeData(
                            response,
                            "ProvinceID",
                            "ProvinceName"
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    @GetMapping("/districts")
    public ResponseEntity<?> getDistricts(
            @RequestParam Integer provinceId
    ) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("province_id", provinceId);

            Map<?, ?> response = callGhn(
                    "/shiip/public-api/master-data/district",
                    body
            );

            return ResponseEntity.ok(
                    normalizeData(
                            response,
                            "DistrictID",
                            "DistrictName"
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    @GetMapping("/wards")
    public ResponseEntity<?> getWards(
            @RequestParam Integer districtId
    ) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("district_id", districtId);

            Map<?, ?> response = callGhn(
                    "/shiip/public-api/master-data/ward",
                    body
            );

            return ResponseEntity.ok(
                    normalizeData(
                            response,
                            "WardCode",
                            "WardName"
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    private Map<?, ?> callGhn(
            String path,
            Map<String, Object> body
    ) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("Chưa cấu hình GHN Token");
        }

        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl(baseUrl)
                    .build();

            Map<?, ?> response = restClient.post()
                    .uri(path)
                    .headers(this::addHeaders)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new RuntimeException(
                        "GHN không trả về dữ liệu địa chỉ"
                );
            }

            return response;

        } catch (RestClientException e) {
            throw new RuntimeException(
                    "Không tải được địa chỉ GHN: "
                            + e.getMessage()
            );
        }
    }

    private void addHeaders(HttpHeaders headers) {
        headers.set("Token", apiKey.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private List<Map<String, String>> normalizeData(
            Map<?, ?> response,
            String codeField,
            String nameField
    ) {
        Object dataObject = response.get("data");

        if (!(dataObject instanceof List<?> dataList)) {
            return new ArrayList<>();
        }

        List<Map<String, String>> result = new ArrayList<>();

        for (Object itemObject : dataList) {
            if (!(itemObject instanceof Map<?, ?> item)) {
                continue;
            }

            Object codeObject = item.get(codeField);
            Object nameObject = item.get(nameField);

            if (codeObject == null || nameObject == null) {
                continue;
            }

            Map<String, String> normalizedItem =
                    new LinkedHashMap<>();

            normalizedItem.put(
                    "code",
                    String.valueOf(codeObject)
            );

            normalizedItem.put(
                    "name",
                    String.valueOf(nameObject)
            );

            result.add(normalizedItem);
        }

        return result;
    }
}