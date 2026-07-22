package com.gxsneaker.gxsneaker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("MYHASH:" + encoder.encode("123456"));
    }
}
