package com.gxsneaker.gxsneaker.controller;

import com.gxsneaker.gxsneaker.dto.ShippingFeeRequestDTO;
import com.gxsneaker.gxsneaker.service.ShippingFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ShippingFeeController {

    private final ShippingFeeService shippingFeeService;

    @PostMapping("/quote")
    public ResponseEntity<?> quote(
            @RequestBody ShippingFeeRequestDTO request
    ) {
        try {
            return ResponseEntity.ok(
                    shippingFeeService.calculateFee(request)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }
}