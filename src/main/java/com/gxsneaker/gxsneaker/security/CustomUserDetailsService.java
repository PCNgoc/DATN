package com.gxsneaker.gxsneaker.security;

import com.gxsneaker.gxsneaker.entity.KhachHang;
import com.gxsneaker.gxsneaker.entity.NhanVien;
import com.gxsneaker.gxsneaker.repository.KhachHangRepository;
import com.gxsneaker.gxsneaker.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // ================= ADMIN / STAFF =================
        Optional<NhanVien> nvOptional =
                nhanVienRepository.findByEmail(username);

        if (nvOptional.isEmpty()) {
            nvOptional = nhanVienRepository.findBySoDienThoai(username);
        }

        if (nvOptional.isPresent()) {
            NhanVien nv = nvOptional.get();

            if (Boolean.FALSE.equals(nv.getTrangThai())) {
                throw new UsernameNotFoundException("Tài khoản nhân viên đã bị khóa");
            }

            String role = normalizeRole(
                    nv.getPhanQuyen() == null
                            ? null
                            : nv.getPhanQuyen().getMaQuyen()
            );

            return new User(
                    nv.getEmail(),
                    nv.getMatKhau(),
                    buildAuthorities(role)
            );
        }

        // ================= CUSTOMER =================
        Optional<KhachHang> khOptional =
                khachHangRepository.findByEmail(username);

        if (khOptional.isEmpty()) {
            khOptional = khachHangRepository.findBySoDienThoai(username);
        }

        if (khOptional.isPresent()) {
            KhachHang kh = khOptional.get();

            if (Boolean.FALSE.equals(kh.getTrangThai())) {
                throw new UsernameNotFoundException("Tài khoản khách hàng đã bị khóa");
            }

            return new User(
                    kh.getEmail(),
                    kh.getMatKhau(),
                    buildAuthorities("CUSTOMER")
            );
        }

        throw new UsernameNotFoundException("Không tìm thấy tài khoản");
    }

    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "CUSTOMER";
        }

        String value = role.trim().toUpperCase();

        if (value.startsWith("ROLE_")) {
            value = value.replace("ROLE_", "");
        }

        if ("NHAN_VIEN".equals(value)) {
            return "STAFF";
        }

        if ("QUAN_TRI".equals(value) || "ADMINISTRATOR".equals(value)) {
            return "ADMIN";
        }

        return value;
    }

    private List<GrantedAuthority> buildAuthorities(String role) {
        String normalizedRole = normalizeRole(role);

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(normalizedRole));
        authorities.add(new SimpleGrantedAuthority("ROLE_" + normalizedRole));

        return authorities;
    }
}