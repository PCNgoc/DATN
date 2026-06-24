package com.gxsneaker.gxsneaker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSanPhamBanChayDTO {

    private String tenSanPham;

    private Long tongSoLuongBan;
}