package com.gxsneaker.gxsneaker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private Integer id;

    private String maKhachHang;

    private String hoTen;

    private String email;

    private String soDienThoai;

    private String message;

    private String role;

    private String token;
}