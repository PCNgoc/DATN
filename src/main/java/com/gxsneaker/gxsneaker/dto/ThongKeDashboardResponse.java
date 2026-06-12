package com.gxsneaker.gxsneaker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDashboardResponse {

    private Long tongSoDon;

    private BigDecimal tongDoanhThu;

    private Long soDonChoXacNhan;

    private Long soDonDaXacNhan;

    private Long soDonDangGiao;

    private Long soDonHoanThanh;

    private Long soDonDaHuy;
}