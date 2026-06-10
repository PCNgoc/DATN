package com.gxsneaker.gxsneaker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichThuocDTO {

    private Long id;
    private String ma;
    private Integer size;
    private Boolean trangThai;
}