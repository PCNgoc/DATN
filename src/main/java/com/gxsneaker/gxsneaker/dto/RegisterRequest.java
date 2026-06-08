package com.gxsneaker.gxsneaker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String hoTen;

    private String email;

    private String soDienThoai;

    private String password;
}